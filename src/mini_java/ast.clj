(ns mini-java.ast
  (:require [clojure.reflect :refer [typename]]
            [mini-java.util :as util])
  (:import [mini_java.antlr MiniJavaParser]))

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
  (into {} (map typeify parser-inner-classes)))

(def ^{:private true} type->key
  (into {} (map (comp vec reverse typeify) parser-inner-classes)))


(defn- children [node]
  (map #(.getChild node %) (range (.getChildCount node))))


(defmulti ast (comp type->key type))

(defmethod ast :default [node]
  node)

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

