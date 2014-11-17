(ns mini-java.code-gen
  (:require [mini-java.ast              :as ast]
            [mini-java.static-semantics :as semantics])
  (:import [org.objectweb.asm
            ClassWriter Opcodes Type]
           [org.objectweb.asm.commons
            GeneratorAdapter Method]))

(def public-static (+ Opcodes/ACC_PUBLIC Opcodes/ACC_STATIC))

(def obj-type (Type/getType Object))

(defn- make-class-writer []
  (ClassWriter. ClassWriter/COMPUTE_FRAMES))

(def ^:private init
  (Method/getMethod "void <init>()"))

(defn- initial-value [type]
  (cond
    :int     (int 0)
    :boolean false
    :else    nil))

(def ^:private primitives
  {:int     "int",
   :int<>   "int[]",
   :boolean "boolean"})

(def ^:private primitive-descriptors
  {:int     "I",
   :int<>   "[I",
   :boolean "Z"})

(defn- type->str [type]
  (get primitives type type))

(defn- type->descriptor [type]
  (or (primitive-descriptors type)
      (str "L" type ";")))

(defn- type->Type [type]
  (Type/getType (type->descriptor type)))

(defn- arg-types [args]
  (clojure.string/join ", " (map (comp type->str :type) args)))

(defn- method-signature [method]
  (str (-> method :type type->str) " " (:name method)
       "(" (arg-types (:args method)) ")"))

(defn- make-method [method]
  (Method/getMethod (method-signature method)))

(defn- make-class
  ([cw class-name]
     (make-class cw class-name "java/lang/Object"))
  ([cw class-name parent]
     (if parent
       (.visit cw Opcodes/V1_1 Opcodes/ACC_PUBLIC class-name nil parent nil)
       (make-class cw class-name))))

(defn- make-constructor
  ([cw]
     (make-constructor cw nil nil))
  ([cw fields class-type]
     (let [init-gen (GeneratorAdapter. Opcodes/ACC_PUBLIC init nil nil cw)]
       (doto init-gen
         (.loadThis)
         (.invokeConstructor obj-type init))
       (when fields
         (doseq [[name field] fields]
           (doto init-gen
             (.loadThis)
             (.push (-> field :type initial-value))
             (.putField class-type
                        name
                        (-> field :type type->Type)))))
       (doto init-gen
         (.returnValue)
         (.endMethod))
       init)))

(defn- locate-arg [name scopes]
  (->> scopes
       :method
       :args
       (filter (fn eq-name [var]
                 (= (:name var)
                    name)))
       first))

(defn- locate-local [name scopes]
  (-> scopes
      :locals
      (get name)))


(defmulti generate (fn [x & _] (ast/context x)))

(defmethod generate :default [x scopes generator]
  (cond
   (= x :this)
   (.loadThis generator)

   :else
   (ast/context x)))

(defmethod generate :main-class-declaration [class scopes]
  (let [class-name (:name class)
        cw   (make-class-writer)
        _    (make-class cw class-name)
        init (make-constructor cw)
        main (Method/getMethod "void main(String[])")
        main-gen (GeneratorAdapter. public-static main nil nil cw)
        main-statement (-> class :methods :main :body)]

    (generate main-statement scopes main-gen)
    (doto main-gen
      (.returnValue)
      (.endMethod))

    ;; finish writing class and return the raw bytes
    (.visitEnd cw)
    (.toByteArray cw)))

(defn- generate-fields [fields class-writer]
  (doseq [[name field] fields]
    (.visitEnd
     (.visitField class-writer
                  Opcodes/ACC_PROTECTED
                  name
                  (-> field :type type->descriptor)
                  nil
                  nil))))

(defmethod generate :class-declaration [class scopes]
  (let [cw (make-class-writer)
        class-type (-> class :name type->Type)
        _  (make-class cw (:name class) (:parent class))
        _  (generate-fields (:vars class) cw)
        init (make-constructor cw (:vars class) class-type)
        scopes (assoc scopes
                 :class      class
                 :class-type class-type)]
    

    ;; generate methods
    (doseq [[name method] (:methods class)]
      (generate method scopes cw))

    ;; finish writing class and return the raw bytes
    (.visitEnd cw)
    (.toByteArray cw)))

(defn- generate-local [var method-gen]
  (let [;; the ASM Type corresponding to var
        type (type->Type (:type var))
        ;; create a new local in the method generator
        index (.newLocal method-gen type)]
    ;; store the index in the var
    (assoc var :index index)))

(defn- generate-locals [vars method-gen]
  (-> (fn [m [name var]]
        (assoc m
          name (if-not (:index var)
                 (generate-local var method-gen)
                 var)))
      (reduce vars vars)))

(defmethod generate :method-declaration [method scopes class-writer]
  (let [meth (make-method method)
        meth-gen (GeneratorAdapter.
                   Opcodes/ACC_PUBLIC meth nil nil class-writer)
        ;; label for tail recursion goto
        start-label (.newLabel meth-gen)
        statements (:body method)
        ;; mapping from name -> local-var-info
        ;; generate-locals creates new locals in the method generator,
        ;; and associates their indices with the local-var-info
        locals (generate-locals (:vars method) meth-gen)
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
    ;; end method
    (.endMethod meth-gen)))

(defmethod generate :nested-statement [statements scopes method-gen]
  (doseq [stat statements]
    (generate stat scopes method-gen)))

(defmethod generate :if-else-statement [statement scopes method-gen]
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
  (let [start-label (.newLabel method-gen)
        end-label   (.newLabel method-gen)]
    ;; start label
    (.mark method-gen start-label)
    ;; push predicate
    (generate (:pred statement) scopes method-gen)
    ;; test predicate
    (.ifZCmp method-gen GeneratorAdapter/EQ end-label)
    ;; while body
    (generate (:body statement) scopes method-gen)
    ;; loop
    (.goTo method-gen start-label)
    ;; end label
    (.mark method-gen end-label)))

(defmethod generate :assign-statement [statement scopes method-gen]
  "Generate a variable assignment statement.

  TODO: Only works with locals, fix to work with class fields too."
  ;; put source of assignment on stack
  (generate (:source statement) scopes method-gen)
  (let [target-name (:target statement)]
    (or
     (when-let [target (locate-arg   target-name scopes)]
       (.storeArg method-gen
                  (:index target))
       true)
     (when-let [target (locate-local target-name scopes)]
       (.storeLocal method-gen
                    (:index target)
                    (-> target :type type->Type))
       true)
     (let [target (semantics/locate-var target-name scopes)]
       (.putField method-gen
                  (:class-type scopes)
                  target-name
                  (-> target :type type->Type))))))

(defmethod generate :print-statement [statement scopes method-gen]
  (.getStatic method-gen
              (Type/getType System)
              "out"
              (Type/getType java.io.PrintStream))
  (generate (:arg statement) scopes method-gen)
  (.invokeVirtual method-gen
                  (Type/getType java.io.PrintStream)
                  (Method/getMethod "void println(int)")))

(defmethod generate :return-statement [statement scopes method-gen label]
  (generate (:return-value statement) scopes method-gen)
  (.returnValue method-gen))

(defn- binary-expression [expression scopes method-gen]
  (generate (:left  expression) scopes method-gen)
  (generate (:right expression) scopes method-gen))

(defmethod generate :add-expression [expression scopes method-gen]
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/ADD Type/INT_TYPE))

(defmethod generate :sub-expression [expression scopes method-gen]
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/SUB Type/INT_TYPE))

(defmethod generate :mul-expression [expression scopes method-gen]
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/MUL Type/INT_TYPE))

(defmethod generate :and-expression [expression scopes method-gen]
  (binary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/AND Type/BOOLEAN_TYPE))

(defmethod generate :lt-expression [expression scopes method-gen]
  (let [true-label (.newLabel method-gen)
        end-label  (.newLabel method-gen)]
    (binary-expression expression scopes method-gen)
    (doto method-gen
      (.ifCmp Type/INT_TYPE GeneratorAdapter/LT true-label)
      ;; not less than, push false and goto end
      (.push false)
      (.goTo end-label)
      ;; less than, jump to true label
      (.mark true-label)
      ;; push true and fall off end
      (.push true)
      (.mark end-label))))

(defn- unary-expression [expression scopes method-gen]
  (generate (:operand expression) scopes method-gen))

(defmethod generate :not-expression [expression scopes method-gen]
  (unary-expression expression scopes method-gen)
  (.not method-gen))

(defmethod generate :neg-expression [expression scopes method-gen]
  (unary-expression expression scopes method-gen)
  (.math method-gen GeneratorAdapter/NEG Type/INT_TYPE))

(defmethod generate :method-call-expression [expression scopes method-gen]
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
                    (Method/getMethod signature))))

(defmethod generate :int-lit-expression [expression scopes method-gen]
  (.push method-gen (:value expression)))

(defmethod generate :boolean-lit-expression [expression scopes method-gen]
  (.push method-gen (:value expression)))



(defmethod generate :identifier-expression [expression scopes method-gen]
  "Load the value bound to the identifier onto the stack.

  NOTE: Only works for locals currently, fix this."
  (or
    ;; load method argument
    (when-let [var (locate-arg (:id expression) scopes)]
      (.loadArg method-gen (:index var))
      true)
    ;; load local variable
    (when-let [var (locate-local (:id expression) scopes)]
      (.loadLocal method-gen (:index var))
      true)
    ;; load non-static field
    (let [field (semantics/locate-var (:id expression) scopes)]
      (.getField method-gen
                 ;; field owner
                 (:class-type scopes)
                 ;; field name
                 (:id expression)
                 ;; field type
                 (type->Type (:type field))))))

(defmethod generate :object-instantiation-expression [expression scopes
                                                      method-gen]
  (let [obj-type (Type/getObjectType (:type expression))]
    (doto method-gen
      (.newInstance obj-type)
      (.dup)
      (.invokeConstructor obj-type init))))

(defn- write-class [name bytes]
  (with-open [o (clojure.java.io/output-stream (str name ".class"))]
    (.write o bytes)))

(defn write-classes [class-table]
  (let [scopes {:class-table class-table}]
    (doseq [[name class] class-table]
      (write-class name (generate class scopes)))))
