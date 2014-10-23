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

(defn- remove-type [[type node]]
  node)

(defn- var-declaration? [[k v]]
  (= k :var-declaration))

(defmulti ast (comp type->key type))

(def ^{:private true} typeless-ast
  (comp remove-type ast))

(defmethod ast :default [node]
  [(type->key (type node)), node])

(defmethod ast :terminal-node [node]
  [:terminal-node, (-> node .-symbol .getText)])

(defmethod ast :int-lit-expression [node]
  [:int-lit-expression,
   (-> node
       (.getChild 0)
       typeless-ast
       Integer.)])

(defmethod ast :boolean-expression [node]
  [:boolean-expression,
   (-> node
       (.getChild 0)
       typeless-ast
       Boolean.)])

(defmethod ast :type [node]
  [:type,
   (-> node
       (.getChild 0)
       typeless-ast)])

(defmethod ast :int-type [node]
  [:int-type, :int])

(defmethod ast :int-array-type [node]
  [:int-array-type, :int-array])

(defmethod ast :boolean-type [node]
  [:boolean-type, :boolean])

(defmethod ast :goal [node]
  (let [children   (children node)
        main-class (first children)
        classes    (-> children rest butlast)]
    [:goal,
     {:main    (typeless-ast main-class),
      :classes (map typeless-ast classes)}]))

(defmethod ast :main-class-declaration [node]
  [:main-class-declaration
   {:name (typeless-ast (.getChild node 1)),
    :body (typeless-ast (.getChild node 2))}])

(defmethod ast :class-declaration [node]
  (let [child? (= 5 (.getChildCount node))
        body-idx (if child? 4 2)]
    [:class-declaration,
     {:name   (typeless-ast (.getChild node 1)),
      :parent (when child? (typeless-ast (.getChild node 3))),
      :body   (typeless-ast (.getChild node body-idx))}]))

(defmethod ast :main-class-body [node]
  (let [body (.getChild node 1)]
    [:main-class-body,
     (typeless-ast body)]))

(defmethod ast :main-method [node]
  [:main-method,
   (ast (.getChild node 2))])

(defmethod ast :class-body [node]
  (let [children (children node)
        declarations (map ast (remove-braces children))]
    [:class-body,
     ; do stuff with this
     declarations]))

(defmethod ast :method-declaration [node]
  [:method-declaration,
   {:name (typeless-ast (.getChild node 2)),
    :type (typeless-ast (.getChild node 1)),
    :args (typeless-ast (.getChild node 3)),
    :body (typeless-ast (.getChild node 4))}])

(defmethod ast :method-body [node]
  (let [children (remove-braces (children node))
        body-nodes (map ast children)
        vars       (filter var-declaration? body-nodes)
        statements (filter (comp not var-declaration?) body-nodes)]
    [:method-body,
     {:vars       vars
      :statements statements}]))

(defmethod ast :var-declaration [node]
  [:var-declaration,
   {:name (typeless-ast (.getChild node 1)),
               :type (typeless-ast (.getChild node 0))}])

(defmethod ast :nested-statement [node]
  [:nested-statement,
   (->> node
        children
        remove-braces
        (map ast))])

(defmethod ast :if-else-statement [node]
  [:if-else-statement
   {:pred (ast (.getChild node 2))
    :then (ast (.getChild node 4))
    :else (ast (.getChild node 6))}])

(defmethod ast :print-statement [node]
  [:print-statement, (typeless-ast (.getChild node 2))])

(defmethod ast :method-call-expression [node]
  [:method-call-expression,
   {:caller (ast          (.getChild node 0)),
    :method (typeless-ast (.getChild node 2)),
    :args   (typeless-ast (.getChild node 3))}])

(defmethod ast :object-instantiation-expression [node]
  [:object-instantiation-expression,
   (typeless-ast (.getChild node 1))])


(defmethod ast :method-argument-list [node]
  (let [children (children node)
        args     (take-nth 2 (-> children rest butlast))]
    [:method-argument-list,
     (map typeless-ast args)]))
