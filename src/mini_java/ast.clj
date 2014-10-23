(ns mini-java.ast
  (:require [clojure.reflect :refer [typename]]
            [mini-java.util :as util])
  (:import [org.antlr.v4.runtime.tree TerminalNodeImpl]
           [mini_java.antlr MiniJavaParser]))

(def ^{:private true} parser-inner-classes
  (.getClasses MiniJavaParser))

(defn- typeify [type]
  (let [str-name (-> type
                     typename
                     (clojure.string/replace #".*MiniJavaParser\$" "")
                     (clojure.string/replace #"Context" "")
                     util/camel->lisp)
        kw-name  (keyword str-name)]
    [kw-name type]))

(def ^{:private true} key->type
  (assoc (into {} (map typeify parser-inner-classes))
    :terminal-node TerminalNodeImpl))

(def ^{:private true} type->key
  (assoc (into {} (map (comp vec reverse typeify) parser-inner-classes))
    TerminalNodeImpl :terminal-node))


(defn- children [node]
  (map #(.getChild node %) (range (.getChildCount node))))

(defn- remove-braces [nodes]
  (-> nodes rest butlast))

(defmulti ast (comp type->key type))

(defmethod ast :default [node]
  (type->key (type node)))

(defmethod ast :terminal-node [node]
  (-> node .-symbol .getText))

(defmethod ast :int-lit-expression [node]
  (new Integer (ast (.getChild node 0))))

(defmethod ast :boolean-expression [node]
  (new Boolean (ast (.getChild node 0))))

(defmethod ast :type [node]
  (ast (.getChild node 0)))

(defmethod ast :int-type [node]
  :int)

(defmethod ast :int-array-type [node]
  :int-array)

(defmethod ast :boolean-type [node]
  :boolean)

(defmethod ast :goal [node]
  (let [children   (children node)
        main-class (first children)
        classes    (-> children rest butlast)]
    {:main     (ast main-class),
     :classes  (map ast classes)}))

(defmethod ast :main-class-declaration [node]
  {:name (ast (.getChild node 1)),
   :body (ast (.getChild node 2))})

(defmethod ast :class-declaration [node]
  (let [child? (= 5 (.getChildCount node))
        body-idx (if child? 4 2)]
    {:name   (ast (.getChild node 1)),
     :parent (when child? (ast (.getChild node 3))),
     :body   (ast (.getChild node body-idx))}))

(defmethod ast :main-class-body [node]
  (let [body (.getChild node 1)]
    (ast body)))

(defmethod ast :main-method [node]
  {:statement (ast (.getChild node 2))})

(defmethod ast :class-body [node]
  (let [children (children node)
        declarations (map ast (remove-braces children))]
    declarations))

(defmethod ast :method-declaration [node]
  (let [children (children node)]
    {:name (ast (.getChild node 2)),
     :type (ast (.getChild node 1)),
     :args (ast (.getChild node 3)),
     :body (ast (.getChild node 4))}))

(defmethod ast :method-body [node]
  (let [children (remove-braces (children node))]
    children))


(defmethod ast :nested-statement [node]
  (let [children (children node)
        statements (remove-braces children)]
    (map ast statements)))

(defmethod ast :print-statement [node]
  {:print-statement (ast (.getChild node 2))})

(defmethod ast :method-call-expression [node]
  {:caller (ast (.getChild node 0)),
   :method (ast (.getChild node 2)),
   :args   (ast (.getChild node 3))})

(defmethod ast :object-instantiation-expression [node]
  {:new-object (ast (.getChild node 1))})


(defmethod ast :method-argument-list [node]
  (let [children (children node)
        args     (take-nth 2 (-> children rest butlast))]
    (map ast args)))
