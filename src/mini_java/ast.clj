(ns mini-java.ast
  (:require [clojure.reflect :refer [typename]]
            [mini-java.keywords :as kw]))

(defn- children [node]
  (map #(.getChild node %) (range (.childCount node))))

(defmulti ast (comp kw/type->key type))

(defmethod ast [:goal] [node]
  (let [children   (children node)
        main-class (first children)
        classes    (-> children rest butlast)]
    {:main     (ast main-class),
     :classes  (map ast classes)}))

(defmethod ast [:main-class-declaration] [node]
  {:name (ast (.getChild node 1)),
   :body (ast (.getChild node 2))})

(defmethod ast [:class-declaration] [node]
  (let [child? (= 5 (.getChildCount node))
        body-idx (if child? 4 2)]
    {:name   (ast (.getChild node 1)),
     :parent (when child? (ast (.getChild node 3))),
     :body   (ast (.getChild node body-idx))}))

(defmethod ast :default [node]
  node)
