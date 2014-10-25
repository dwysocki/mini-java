(ns mini-java.ast
  (:require [clojure.reflect :refer [typename]]
            [mini-java.util :as util])
  (:import [org.antlr.v4.runtime.tree TerminalNodeImpl]
           [mini_java.antlr MiniJavaParser]))

(defn- node-line-and-column [node]
  (let [token (.getStart node)]
    (util/token-line-and-column token)))

(defn- with-line-and-column [node ctx obj]
  (let [[line column] (node-line-and-column node)]
    (with-meta obj
      {:line line
       :column column
       :context ctx})))

(defn context [node]
  (:context (meta node)))

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

(def ^{:private true} obj-type-key
  (comp type->key type))

(defn- children [node]
  (map #(.getChild node %) (range (.getChildCount node))))

(defn- remove-braces [nodes]
  (-> nodes rest butlast))

(defn- remove-type [[type node]]
  node)

(defn- var-declaration? [[k v]]
  (= k :var-declaration))

(defn- method-declaration? [[k v]]
  (= k :method-declaration))

(defmulti ast obj-type-key)

(defmethod ast :default [node]
  [(type->key (type node)), node])

(defmethod ast :terminal-node [node]
  (-> node .-symbol .getText))



(defmethod ast :goal [node]
  (let [children   (children node)
        main-class (first children)
        classes    (-> children rest butlast)]
    (with-line-and-column node :goal
      {:main    (ast main-class),
       :classes (map ast classes)})))

(defmethod ast :main-class-declaration [node]
  (with-line-and-column node :main-class-declaration
    {:name (ast (.getChild node 1)),
     :body (ast (.getChild node 2))}))

(defmethod ast :class-declaration [node]
  (let [child? (= 5 (.getChildCount node))
        body-idx (if child? 4 2)
        {:keys [vars methods]} (ast (.getChild node body-idx))]
    (with-line-and-column node :class-declaration
     {:name    (ast (.getChild node 1)),
      :parent  (when child? (ast (.getChild node 3))),
      :vars    (map remove-type vars),
      :methods (map remove-type methods)})))

(defmethod ast :main-class-body [node]
  (ast (.getChild node 1)))

(defmethod ast :main-method [node]
  (ast (.getChild node 2)))

(defmethod ast :class-body [node]
  (let [children     (children node)
        declarations (map ast (remove-braces children))
        vars         (filter var-declaration? declarations)
        methods      (filter method-declaration? declarations)]
    (with-line-and-column node :class-body
     {:vars    vars,
      :methods methods})))

(defmethod ast :method-declaration [node]
  (let [{:keys [vars statements]} (ast (.getChild node 4))]
    [:method-declaration,
     (with-line-and-column node :method-declaration
      {:name (ast (.getChild node 2)),
       :type (ast (.getChild node 1)),
       :args (ast (.getChild node 3)),
       :vars vars,
       :statements statements})]))

(defmethod ast :method-body [node]
  (let [children (remove-braces (children node))
        body-nodes (map ast children)]
    (with-line-and-column node :method-body
     {:vars       (map remove-type (filter var-declaration? body-nodes))
      :statements (filter (comp not var-declaration?) body-nodes)})))

(defmethod ast :var-declaration [node]
  [:var-declaration,
   (with-line-and-column node :var-declaration
    {:name (ast (.getChild node 1)),
     :type (ast (.getChild node 0))})])

(defmethod ast :nested-statement [node]
  [:nested-statement,
   (with-line-and-column node :nested-statement
    (->> node
         children
         remove-braces
         (map ast)))])

(defmethod ast :if-else-statement [node]
  [:if-else-statement
   (with-line-and-column node :if-else-statement
    {:pred (ast (.getChild node 2)),
     :then (ast (.getChild node 4)),
     :else (ast (.getChild node 6))})])

(defmethod ast :while-statement [node]
  [:while-statement,
   (with-line-and-column node :while-statement
    {:pred (ast (.getChild node 2)),
     :body (ast (.getChild node 4))})])

(defmethod ast :print-statement [node]
  [:print-statement,
   (with-line-and-column node :print-statement
     {:arg (ast (.getChild node 2))})])

(defmethod ast :assign-statement [node]
  [:assign-statement,
   (with-line-and-column node :assign-statement
    {:target (ast (.getChild node 0)),
     :source (ast (.getChild node 2))})])

(defmethod ast :array-assign-statement [node]
  [:array-assign-statement,
   (with-line-and-column node :array-assign-statement
    {:target (ast (.getChild node 0)),
     :index  (ast (.getChild node 2)),
     :source (ast (.getChild node 5))})])

(defmethod ast :return-statement [node]
  [:return-statement,
   (with-line-and-column node :return-statement
    {:return-value (ast (.getChild node 1))})])

(defmethod ast :recur-statement [node]
  [:recur-statement,
   (with-line-and-column node :recur-statement
    {:pred (ast (.getChild node 1)),
     :args (ast (.getChild node 3)),
     :base (ast (.getChild node 5))})])

(defmethod ast :method-argument-list [node]
  (let [children (children node)
        args     (take-nth 2 (-> children rest butlast))]
    (with-line-and-column node :method-argument-list
     (map ast args))))

(defmethod ast :formal-parameters [node]
  (with-line-and-column node :formal-parameters
   (let [length (.getChildCount node)]
     (if (= 3 length)
       (ast (.getChild node 1))
       []))))

(defmethod ast :formal-parameter-list [node]
  (->> node
       children
       (take-nth 2) ; ignore commas
       (map ast)))

(defmethod ast :formal-parameter [node]
  (with-line-and-column node :formal-parameter
   {:type (ast (.getChild node 0)),
    :name (ast (.getChild node 1))}))


(defmethod ast :type [node]
  (ast (.getChild node 0)))

(defn- unary-expression [node]
  (with-line-and-column node (obj-type-key node)
   {:operand (ast (.getChild node 1))}))

(defn- binary-expression [node]
  (with-line-and-column node (obj-type-key node)
   {:left  (ast (.getChild node 0)),
    :right (ast (.getChild node 2))}))

(defmethod ast :and-expression [node]
  [:and-expression,
   (binary-expression node)])

(defmethod ast :lt-expression [node]
  [:lt-expression,
   (binary-expression node)])

(defmethod ast :add-expression [node]
  [:add-expression,
   (binary-expression node)])

(defmethod ast :sub-expression [node]
  [:sub-expression,
   (binary-expression node)])

(defmethod ast :mul-expression [node]
  [:mul-expression,
   (binary-expression node)])

(defmethod ast :array-access-expression [node]
  [:array-access-expression,
   (with-line-and-column node :array-access-expression
    {:array (ast (.getChild node 0)),
     :index (ast (.getChild node 2))})])

(defmethod ast :array-length-expression [node]
  [:array-length-expression,
   (with-line-and-column node :array-length-expression
     (ast (.getChild node 0)))])

(defmethod ast :method-call-expression [node]
  [:method-call-expression,
   (with-line-and-column node :method-call-expression
    {:caller (ast (.getChild node 0)),
     :method (ast (.getChild node 2)),
     :args   (ast (.getChild node 3))})])

(defmethod ast :int-lit-expression [node]
  [:int-lit-expression,
   (with-line-and-column node :int-lit-expression
     {:value (-> node
                 (.getChild 0)
                 ast
                 Integer.)})])

(defmethod ast :boolean-expression [node]
  [:boolean-expression,
   (with-line-and-column node :boolean-expression
     {:value (-> node
                 (.getChild 0)
                 ast
                 Boolean.)})])

(defmethod ast :identifier-expression [node]
  [:identifier-expression,
   (with-line-and-column node :identifier-expression
     {:id (ast (.getChild node 0))})])

(defmethod ast :this-expression [node]
  :this)

(defmethod ast :array-instantiation-expression [node]
  [:array-instantiation-expression,
   (with-line-and-column node :array-instantiation-expression
     {:size (ast (.getChild node 3))})])

(defmethod ast :object-instantiation-expression [node]
  [:object-instantiation-expression,
   (with-line-and-column node :object-instantiation-expression
     {:type (ast (.getChild node 1))})])

(defmethod ast :not-expression [node]
  [:not-expression,
   (unary-expression node)])

(defmethod ast :neg-expression [node]
  [:neg-expression,
   (unary-expression node)])

(defmethod ast :paren-expression [node]
  (ast (.getChild node 1)))


(defmethod ast :int-type [node]
  :int)

(defmethod ast :int-array-type [node]
  :int-array)

(defmethod ast :boolean-type [node]
  :boolean)
