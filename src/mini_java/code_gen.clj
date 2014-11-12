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

(defn- make-constructor [cw]
  (let [init-gen (GeneratorAdapter. Opcodes/ACC_PUBLIC init nil nil cw)]
    (doto init-gen
      (.loadThis)
      (.invokeConstructor obj-type init)
      (.returnValue)
      (.endMethod))
    init))

(def ^:private primitives
  {:int     "int",
   :int<>   "int[]",
   :boolean "boolean"})

(defn- type->str [type]
  (get primitives type type))

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



(defmulti generate (fn [x & _] (ast/context x)))

(defmethod generate :default [x & _]
  (ast/context x))

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

(defmethod generate :class-declaration [class scopes]
  (let [cw (make-class-writer)
        _  (make-class cw (:name class) (:parent class))
        init (make-constructor cw)]
    ;;;; TODO: add class fields ;;;;

    ;; generate methods
    (doseq [[name method] (:methods class)]
      (generate method scopes cw))

    ;; finish writing class and return the raw bytes
    (.visitEnd cw)
    (.toByteArray cw)))

(defmethod generate :method-declaration [method scopes class-writer]
  (let [meth (make-method method)
        meth-gen (GeneratorAdapter.
                   Opcodes/ACC_PUBLIC meth nil nil class-writer)
        start-label (.newLabel meth-gen)
        statements (:body method)]
    ;;;; TODO: add method variables ;;;;

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
