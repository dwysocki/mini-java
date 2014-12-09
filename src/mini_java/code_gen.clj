(ns mini-java.code-gen
  "Generate Java bytecode from a valid class table built during
  static semantics."
  (:require [mini-java.ast              :as ast]
            [mini-java.static-semantics :as semantics])
  (:import [org.objectweb.asm
            ClassWriter Opcodes Type]
           [org.objectweb.asm.commons
            GeneratorAdapter Method]))

;; handy shortcuts
(def public-static (+ Opcodes/ACC_PUBLIC Opcodes/ACC_STATIC))
(def obj-type (Type/getType Object))

(defn- make-class-writer
  "Instantiate an ASM ClassWriter."
  []
  (ClassWriter. ClassWriter/COMPUTE_FRAMES))

(def ^:private init
  (Method/getMethod "void <init>()"))

(defn- initial-value
  "Returns the initial value of an object field with the given type."
  [type]
  (case type
    :int     (int 0)
    :boolean false
    nil))

(def ^:private primitives
  "Maps primitive keywords to Java type names"
  {:int     "int",
   :int<>   "int[]",
   :boolean "boolean"})

(def ^:private primitive-descriptors
  "Maps primitive keywords to ASM type descriptors"
  {:int     "I",
   :int<>   "[I",
   :boolean "Z"})

(defn- type->str
  "Maps a given type to its Java type name.

  Input type can be a string or a primitive keyword.
  Primitives are mapped in the primitives map, and all other types are
  left as-is."
  [type]
  (get primitives type type))

(defn- type->descriptor
  "Maps a given type to its ASM type descriptor.
  Primitives have specific descriptors given by primitive-descriptors,
  all other descriptors are just the type prefixed with an L, and suffixed
  with a semicolon."
  [type]
  (or (primitive-descriptors type)
      (str "L" type ";")))

(defn- type->Type
  "Maps a type to its ASM Type object."
  [type]
  (Type/getType (type->descriptor type)))

(defn- arg-types
  "Returns a comma separated list of the types of the arg list."
  [args]
  (clojure.string/join ", "
                       (map (comp type->str :type) args)))

(defn- method-signature
  "Returns a string representation of the given method's signature.
  Method must be a class-table method representation."
  [method]
  (str (-> method :type type->str) " " (:name method)
       "(" (arg-types (:args method)) ")"))

(defn- make-method
  "Creates an ASM Method object given a class-table method representation."
  [method]
  (Method/getMethod (method-signature method)
                    true))

(defn- make-class
  "Visits the given ClassWriter, making it a public class with the given
  class name and parent. If no parent is given, defaults to Object."
  ([cw class-name]
     (make-class cw class-name "java/lang/Object"))
  ([cw class-name parent]
     (if parent
       (.visit cw Opcodes/V1_1 Opcodes/ACC_PUBLIC class-name nil parent nil)
       (make-class cw class-name))))

(defn- make-constructor
  "Returns a constructor Method for the given ClassWriter class."
  ([cw]
     (make-constructor cw obj-type))
  ([cw parent-type]
     (let [init-gen (GeneratorAdapter. Opcodes/ACC_PUBLIC init nil nil cw)]
       (doto init-gen
         (.loadThis)
         (.invokeConstructor parent-type init)
         (.returnValue)
         (.endMethod))
       init)))

(defn- locate-arg
  "Searches for the variable name in the argument list of the current method
  by searching through the scopes map. Returns the variable representation
  if found, nil otherwise."
  [name scopes]
  (->> scopes
       :method
       :args
       (filter (fn eq-name [var]
                 (= (:name var)
                    name)))
       first))

(defn- locate-local
  "Searches for the variable name in the locals of the current method
  by searching through the scopes map. Returns the variable representation
  if found, nil otherwise."
  [name scopes]
  (-> scopes
      :locals
      (get name)))


(defmulti generate
  "Dispatch function for generating the code for a node of the class-table.
  Dispatches on the context metadata of the first argument."
  (fn [x & _] (ast/context x)))

(defmethod generate :default [x scopes generator]
  "Generates the code for an x which has no known context. This is an error,
  unless x is :this, in which case the generator must load this onto the stack."
  (cond
   (= x :this)
   (.loadThis generator)

   :else
   (throw (ex-info "Unknown context"
                   {:type   :unknown-context,
                    :node   x,
                    :scopes scopes}))))

(defmethod generate :main-class-declaration [class scopes]
  "Generates the bytecode for the main class."
  (let [;; create a class writer
        cw   (make-class-writer)
        ;; visit the class writer to give it the class metadata
        _    (make-class cw (:name class))
        ;; create the constructor ASM Method
        init (make-constructor cw)
        ;; create the main ASM Method
        main (Method/getMethod "void main(String[])")
        ;; create the Generator for the main Method
        main-gen (GeneratorAdapter. public-static main nil nil cw)
        ;; extract the single main statement from the class
        main-statement (-> class :methods :main :body)]
    ;; generate the code for the single main statement
    (generate main-statement scopes main-gen)
    ;; end the main method
    (doto main-gen
      (.returnValue)
      (.endMethod))

    ;; finish writing class and return the raw bytes
    (.visitEnd cw)
    (.toByteArray cw)))

(defn- generate-fields [fields class-writer]
  "Generates the fields of a class."
  (doseq [[name field] fields]
    (.visitEnd
     (.visitField class-writer
                  Opcodes/ACC_PROTECTED
                  name
                  (-> field :type type->descriptor)
                  nil
                  nil))))

(defmethod generate :class-declaration [class scopes]
  "Generates the bytecode for a non-main class."
  (let [;; create a class writer
        cw (make-class-writer)
        ;; create a Type object from the class's name
        class-type (-> class :name type->Type)
        ;; create a Type object from the class's parent's name
        ;; or Object if none given
        parent-type (if-let [parent (:parent class)]
                      (type->Type parent)
                      obj-type)
        ;; visit the class writer to give it the class metadata
        ;; and generate the class' fields
        _  (make-class cw (:name class) (:parent class))
        _  (generate-fields (:vars class) cw)
        ;; create the class' constructor Method
        init (make-constructor cw parent-type)
        ;; add the class, its type, and its parents to the existing scopes
        scopes (assoc scopes
                 :class      class
                 :class-type class-type
                 :parents    (semantics/parent-seq class
                                                   (:class-table scopes)))]
    

    ;; generate methods
    (doseq [[name method] (:methods class)]
      (generate method scopes cw))

    ;; finish writing class and return the raw bytes
    (.visitEnd cw)
    (.toByteArray cw)))

(defn- generate-local
  "Generates the bytecode for a local variable.

  The method generator is informed that there is a new local, and it assigns
  to it a unique index. This index is associated with the class-table
  representation of the local, and the updated local is returned."
  [var method-gen]
  (let [;; create the ASM Type corresponding to var
        type (type->Type (:type var))
        ;; create a new local in the method generator
        index (.newLocal method-gen type)]
    ;; store the index in the var
    (assoc var :ref-index index)))

(defn- generate-locals
  "Generates the bytecode for each local variable in a method, and return
  an updated map of the method's variables.

  This does not affect the bytecode, but gives the method generator knowledge
  of the variables, and alters the variable map to include a unique reference
  index for each variable, for lookup later."
  [vars method-gen]
  (-> (fn [m [name var]]
        (assoc m
          name (if (:arg-index var)
                 ;; var is part of the argument list, do nothing with it
                 var
                 ;; var is a local, update it with a unique index
                 (generate-local var method-gen))))
      (reduce vars vars)))

(defmethod generate :method-declaration [method scopes class-writer]
  "Generates the bytecode for a method."
  (let [;; create an ASM Method for the given method
        meth (make-method method)
        ;; create the Generator
        meth-gen (GeneratorAdapter.
                   Opcodes/ACC_PUBLIC meth nil nil class-writer)
        ;; create label for tail recursion goto
        start-label (.newLabel meth-gen)
        statements (:body method)
        ;; mapping from name -> local-var-info
        ;; generate-locals creates new locals in the method generator,
        ;; and associates their indices with the local-var-info
        locals (generate-locals (:vars method) meth-gen)
        ;; add the method's locals and the method itself to the scopes,
        ;; to give the method's statements the appropriate context
        scopes (assoc scopes
                 :locals locals
                 :method method)]
    ;; set start label for recur statement
    (.mark meth-gen start-label)
    ;; generate statements
    (doseq [statement (butlast statements)]
      (generate statement scopes meth-gen))
    ;; generate return/recur statement
    (generate (last statements) scopes meth-gen start-label)
    ;; end the method
    (.endMethod meth-gen)))

(defmethod generate :nested-statement [statements scopes method-gen]
  "Generates the bytecode for a nested statement.

  Simply generates the bytecode for each statement nested within it."
  (doseq [stat statements]
    (generate stat scopes method-gen)))

(defmethod generate :if-else-statement [statement scopes method-gen]
  "Generates the bytecode for an if/else statement.

  This is handled in the least optimized, most general way possible.
  Pushes the predicate onto the stack, and then jumps to the else label if
  the predicate is false, or falls through to the then part, which jumps to
  the end of the else part after executing."
  (let [else-label (.newLabel method-gen)
        end-label  (.newLabel method-gen)]
    (generate (:pred statement) scopes method-gen)
    ;; branch
    (.ifZCmp method-gen GeneratorAdapter/EQ else-label)
    ;; then part
    (generate (:then statement) scopes method-gen)
    (.goTo method-gen end-label)
    ;; else part
    (.mark method-gen else-label)
    (generate (:else statement) scopes method-gen)
    (.mark method-gen end-label)))

(defmethod generate :while-statement [statement scopes method-gen]
  "Generates the bytecode for a while statement.
  
  This is done by setting a label at the beginning, evaluating the predicate,
  jumping to the end label if false, and otherwise falling through to the
  body, which jumps back to the beginning after executing."
  (let [start-label (.newLabel method-gen)
        end-label   (.newLabel method-gen)]
    ;; start label
    (.mark method-gen start-label)
    ;; push predicate
    (generate (:pred statement) scopes method-gen)
    ;; test predicate, go past body if false
    (.ifZCmp method-gen GeneratorAdapter/EQ end-label)
    ;; while body
    (generate (:body statement) scopes method-gen)
    ;; loop
    (.goTo method-gen start-label)
    ;; end label
    (.mark method-gen end-label)))

(defmethod generate :assign-statement [statement scopes method-gen]
  "Generates the bytecode for a variable assignment statement.

  Tests whether the target of the assignment is a method argument, local,
  or a field of this class, and then generates the code to assign the
  source to that target in the appropriate manner."
  (let [target-name (:target statement)]
    (or
     (when-let [target (locate-arg target-name scopes)]
       ;; put source of assignment on stack
       (generate (:source statement) scopes method-gen)
       (.storeArg method-gen
                  (:arg-index target))
       true)
     (when-let [target (locate-local target-name scopes)]
         ;; put source of assignment on stack
       (generate (:source statement) scopes method-gen)
       (.storeLocal method-gen
                    (:ref-index target)
                    (-> target :type type->Type))
       true)
     (let [target (semantics/locate-var target-name scopes)]
       (.loadThis method-gen)
       ;; put source of assignment on stack
       (generate (:source statement) scopes method-gen)
       ;; store field
       (.putField method-gen
                  (:class-type scopes)
                  target-name
                  (-> target :type type->Type))))))

(defmethod generate :array-assign-statement [statement scopes method-gen]
  "Generates the bytecode for an array assignment statement.

  Tests whether the target of the assignment is a method argument, local,
  or a field of this class, and then generates the code to assign the
  source to that target's given index in the appropriate manner."
  (let [target-name (:target statement)]
    ;; put array reference on stack
    (or
     ;; array is an argument of the method
     (when-let [target (locate-arg target-name scopes)]
       (.loadArg method-gen
                 (:arg-index target))
       true)
     ;; array is a local of the method
     (when-let [target (locate-local target-name scopes)]
       (.loadLocal method-gen
                   (:ref-index target)
                   (-> target :type type->Type))
       true)
     ;; array is a field of the class
     (let [target (semantics/locate-var target-name scopes)]
       (.loadThis method-gen)
       (.getField method-gen
                  (:class-type scopes)
                  target-name
                  (-> target :type type->Type))))
    ;; put array index on stack
    (generate (:index statement) scopes method-gen)
    ;; put value to store in array on stack
    (generate (:source statement) scopes method-gen)
    ;; store value in array
    (.arrayStore method-gen Type/INT_TYPE)))

(defmethod generate :print-statement [statement scopes method-gen]
  "Generates the bytecode for an integer print statement."
  ;; load the static PrintStream field of System.out
  (.getStatic method-gen
              (Type/getType System)
              "out"
              (Type/getType java.io.PrintStream))
  ;; generate the code to be printed
  (generate (:arg statement) scopes method-gen)
  ;; call the println(int) method
  (.invokeVirtual method-gen
                  (Type/getType java.io.PrintStream)
                  (Method/getMethod "void println(int)")))

(defmethod generate :return-statement [statement scopes method-gen label]
  "Generates the bytecode for a return statement."
  ;; generate the code for the return value
  (generate (:return-value statement) scopes method-gen)
  ;; return the value at the top of the stack
  (.returnValue method-gen))

(defn- rebind-arg
  "Rebinds the given method argument for the recur statement."
  [argument index scopes method-gen]
  (generate argument scopes method-gen)
  (.storeArg method-gen index))

(defmethod generate :recur-statement [statement scopes method-gen start-label]
  "Generates the bytecode for a recur statement.

  If the predicate is false, jumps to the base case, otherwise falls through
  to the recursion case. For the recursion case, evaluates each of the
  recursion arguments in order, placing the values on the stack, and then
  rebinds them in reverse order. The base case simply returns the result of the
  expression."
  (let [base-label (.newLabel method-gen)]
    (generate (:pred statement) scopes method-gen)
    ;; if predicate is false, goto base case
    (.ifZCmp method-gen GeneratorAdapter/EQ base-label)
    ;; when predicate is true, evaluate arguments, rebind and recur:
    ;; evaluate arguments
    (doseq [arg (:args statement)]
      (generate arg scopes method-gen))
    ;; rebind arguments
    (doseq [index (-> statement :args count range reverse)]
      (.storeArg method-gen index))
    ;; recur
    (.goTo method-gen start-label)
    ;; base case
    (.mark method-gen base-label)
    (generate (:base statement) scopes method-gen)
    (.returnValue method-gen)))

(defmethod generate :array-access-expression [expression scopes method-gen]
  "Generates the bytecode for an array access."
  (generate (:array expression) scopes method-gen)
  (generate (:index expression) scopes method-gen)
  (.arrayLoad method-gen Type/INT_TYPE))

(defmethod generate :array-length-expression [expression scopes method-gen]
  "Generates the bytecode for an array length expression."
  ;; load array reference on stack
  (generate (:array expression) scopes method-gen)
  ;; load length of array reference on stack
  (.arrayLength method-gen))

(defn- binary-expression
  "Helper function for generating the bytecode for a binary expression.
  Generates bytecode for the left hand side of the expression, then the
  right hand side of the expression."
  [expression scopes method-gen]
  (generate (:left  expression) scopes method-gen)
  (generate (:right expression) scopes method-gen))

(defmethod generate :add-expression [expression scopes method-gen]
  "Generates the bytecode for an addition expression."
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/ADD Type/INT_TYPE))

(defmethod generate :sub-expression [expression scopes method-gen]
  "Generates the bytecode for a subtraction expression."
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/SUB Type/INT_TYPE))

(defmethod generate :mul-expression [expression scopes method-gen]
  "Generates the bytecode for a multiplication expression."
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/MUL Type/INT_TYPE))

(defmethod generate :and-expression [expression scopes method-gen]
  "Generates the bytecode for a logical and expression."
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/AND Type/BOOLEAN_TYPE))

(defmethod generate :lt-expression [expression scopes method-gen]
  "Generates the bytecode for a less than expression.

  This was the most involved binary operator, as it involved a conditional:
  either push true or false onto the stack."
  (let [true-label (.newLabel method-gen)
        end-label  (.newLabel method-gen)]
    (binary-expression expression scopes method-gen)
    (doto method-gen
      ;; compare the top two values on the stack
      (.ifCmp Type/INT_TYPE GeneratorAdapter/LT true-label)
      ;; not less than, push false and goto end
      (.push false)
      (.goTo end-label)
      ;; less than, jump to true label
      (.mark true-label)
      ;; push true and fall off end
      (.push true)
      (.mark end-label))))

(defn- unary-expression
  "Helper function for generating the bytecode for a unary expression.
  Generates the bytecode for the operand."
  [expression scopes method-gen]
  (generate (:operand expression) scopes method-gen))

(defmethod generate :not-expression [expression scopes method-gen]
  "Generate the bytecode for a not expression."
  (unary-expression expression scopes method-gen)
  (.not method-gen))

(defmethod generate :neg-expression [expression scopes method-gen]
  "Generates the bytecode for a unary minus expression."
  (unary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/NEG Type/INT_TYPE))

(defmethod generate :array-instantiation-expression [expression scopes
                                                     method-gen]
  "Generates the bytecode for an int array instantiation expression."
  (generate (:size expression) scopes method-gen)
  (.newArray method-gen Type/INT_TYPE))

(defmethod generate :method-call-expression [expression scopes method-gen]
  "Generates the bytecode for a method call expression."
  ;; push caller onto stack
  (generate (:caller expression) scopes method-gen)
  ;; push method arguments onto stack
  (doseq [arg (:args expression)]
    (generate arg scopes method-gen))

  (let [caller-type (semantics/type-check (:caller expression) scopes nil)
        caller-class (-> scopes :class-table (get caller-type))
        method (semantics/locate-method caller-class
                                        (:method expression)
                                        scopes)
        signature (method-signature method)]
    (.invokeVirtual method-gen
                    (Type/getObjectType caller-type)
                    (Method/getMethod signature true))))

(defmethod generate :int-lit-expression [expression scopes method-gen]
  "Generates the bytecode for an integer literal expression.

  Loads the literal onto the stack."
  (.push method-gen (:value expression)))

(defmethod generate :boolean-lit-expression [expression scopes method-gen]
  "Generates the bytecode for a boolean literal expression.

  Loads the literal onto the stack."
  (.push method-gen (:value expression)))

(defmethod generate :identifier-expression [expression scopes method-gen]
  "Generates the bytecode for an identifier expression.

  Loads the value of the identifier onto the stack."
  (or
    ;; load method argument
    (when-let [var (locate-arg (:id expression) scopes)]
      (.loadArg method-gen (:arg-index var))
      true)
    ;; load local variable
    (when-let [var (locate-local (:id expression) scopes)]
      (.loadLocal method-gen (:ref-index var))
      true)
    ;; load non-static field
    (let [field (semantics/locate-var (:id expression) scopes)]
      (.loadThis method-gen)
      (.getField method-gen
                 ;; field owner
                 (:class-type scopes)
                 ;; field name
                 (:id expression)
                 ;; field type
                 (type->Type (:type field))))))

(defmethod generate :object-instantiation-expression [expression scopes
                                                      method-gen]
  "Generates the bytecode for an object instantiation expression.

  Pushes two instances of a new object of the given type, and then invokes
  the constructor of that type, storing it over the first instance."
  (let [type (Type/getObjectType (:type expression))]
    (doto method-gen
      (.newInstance type)
      (.dup)
      (.invokeConstructor type init))))

(defn- write-class
  "Writes the bytecode of a single class to a file in the given directory."
  [name directory bytes]
  (with-open [o (->> (str name ".class")
                     (clojure.java.io/file directory)
                     clojure.java.io/output-stream)]
    (.write o bytes)))

(defn write-classes
  "Generates and writes the bytecode of each class in the class table to
  files in the given directory."
  [class-table directory]
  (let [scopes {:class-table class-table}]
    (doseq [[name class] class-table]
      (write-class name directory (generate class scopes)))))
