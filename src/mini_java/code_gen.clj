(ns mini-java.code-gen
  (:require [mini-java.ast :as ast])
  (:import [org.objectweb.asm
            ClassWriter Opcodes Type]
           [org.objectweb.asm.commons
            GeneratorAdapter Method]))

(def public-static (+ Opcodes/ACC_PUBLIC Opcodes/ACC_STATIC))

(def obj-type (Type/getType Object))

(defn- make-class-writer []
  (ClassWriter. ClassWriter/COMPUTE_FRAMES))

(defn- make-constructor [cw]
  (let [init     (Method/getMethod "void <init>()")
        init-gen (GeneratorAdapter. Opcodes/ACC_PUBLIC init nil nil cw)]
    (doto init-gen
      (.loadThis)
      (.invokeConstructor obj-type init)
      (.returnValue)
      (.endMethod))
    init))

(defn- make-class
  ([cw class-name]
     (make-class cw class-name "java/lang/Object"))
  ([cw class-name parent]
     (.visit cw Opcodes/V1_1 Opcodes/ACC_PUBLIC class-name nil parent nil)))

(defmulti generate (fn [x & _] (ast/context x)))

(defmethod generate :default [x & _]
  (ast/context x))

(defmethod generate :main-class-declaration [class class-table]
  (let [class-name (:name class)
        cw   (make-class-writer)
        _    (make-class cw class-name)
        init (make-constructor cw)
        main (Method/getMethod "void main(String[])")
        main-gen (GeneratorAdapter. public-static main nil nil cw)
        main-statement (-> class :methods :main :body)]

    (generate main-statement class-table main-gen)
    (doto main-gen
      (.returnValue)
      (.endMethod))

    (.visitEnd cw)

    (.toByteArray cw)))

(defmethod generate :nested-statement [statements class-table method-gen]
  (doseq [stat statements]
    (generate stat class-table method-gen)))

(defmethod generate :if-else-statement [statement class-table method-gen]
  (let [else-label (.newLabel method-gen)
        end-label  (.newLabel method-gen)]
    (generate (:pred statement) class-table method-gen)
    ;; branch
    (.ifZCmp method-gen GeneratorAdapter/EQ else-label)
    ;; then part
    (generate (:then statement) class-table method-gen)
    (.goTo method-gen end-label)
    ;; else part
    (.mark method-gen else-label)
    (generate (:else statement) class-table method-gen)
    (.mark method-gen end-label)))

(defmethod generate :while-statement [statement class-table method-gen]
  (let [start-label (.newLabel method-gen)
        end-label   (.newLabel method-gen)]
    ;; start label
    (.mark method-gen start-label)
    ;; push predicate
    (generate (:pred statement) class-table method-gen)
    ;; test predicate
    (.ifZCmp method-gen GeneratorAdapter/EQ end-label)
    ;; while body
    (generate (:body statement) class-table method-gen)
    ;; loop
    (.goTo method-gen start-label)
    ;; end label
    (.mark method-gen end-label)))

(defmethod generate :print-statement [statement class-table method-gen]
  (.getStatic method-gen
              (Type/getType System)
              "out"
              (Type/getType java.io.PrintStream))
  (generate (:arg statement) class-table method-gen)
  (.invokeVirtual method-gen
                  (Type/getType java.io.PrintStream)
                  (Method/getMethod "void println(int)")))

(defn- binary-expression [expression class-table method-gen]
  (generate (:left  expression) class-table method-gen)
  (generate (:right expression) class-table method-gen))

(defmethod generate :add-expression [expression class-table method-gen]
  (binary-expression expression class-table method-gen)
  (.math method-gen GeneratorAdapter/ADD Type/INT_TYPE))

(defmethod generate :sub-expression [expression class-table method-gen]
  (binary-expression expression class-table method-gen)
  (.math method-gen GeneratorAdapter/SUB Type/INT_TYPE))

(defmethod generate :mul-expression [expression class-table method-gen]
  (binary-expression expression class-table method-gen)
  (.math method-gen GeneratorAdapter/MUL Type/INT_TYPE))

(defmethod generate :and-expression [expression class-table method-gen]
  (binary-expression expression class-table method-gen)
  (.math method-gen GeneratorAdapter/AND Type/BOOLEAN_TYPE))

(defmethod generate :lt-expression [expression class-table method-gen]
  (let [true-label (.newLabel method-gen)
        end-label  (.newLabel method-gen)]
    (binary-expression expression class-table method-gen)
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

(defn- unary-expression [expression class-table method-gen]
  (generate (:operand expression) class-table method-gen))

(defmethod generate :not-expression [expression class-table method-gen]
  (unary-expression expression class-table method-gen)
  (.not method-gen))

(defmethod generate :neg-expression [expression class-table method-gen]
  (unary-expression expression class-table method-gen)
  (.math method-gen GeneratorAdapter/NEG Type/INT_TYPE))

(defmethod generate :int-lit-expression [expression class-table method-gen]
  (.push method-gen (:value expression)))

(defmethod generate :boolean-lit-expression [expression class-table method-gen]
  (.push method-gen (:value expression)))

(defn byte-codes [class-table]
  (->> (for [[name class] class-table]
         [name (generate class class-table)])
       (into {})))

(defn- write-class [name bytes]
  (with-open [o (clojure.java.io/output-stream (str name ".class"))]
    (.write o bytes)))

(defn write-classes [class-table]
  (doseq [[name class] class-table]
    (write-class name (generate class class-table))))
