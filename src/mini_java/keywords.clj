(ns mini-java.keywords
  (:require [clojure.reflect :refer [reflect typename]]
            [mini-java.util  :as util])
  (:import [mini_java.antlr MiniJavaParser]))

(def ^{:private true} reflected-parser
  (reflect MiniJavaParser))

(def ^{:private true} members
  (:members reflected-parser))

(defn- typeify [member]
  (let [type-name (:name member)
        str-name  (-> type-name
                      typename
                      (clojure.string/replace #"Context" "")
                      util/camel->lisp)
        kw-name   (keyword str-name)]
    [kw-name type-name]))

(def key->type
  (into {} (map typeify members)))

(def type->key
  (into {} (map (comp vec reverse typeify) members)))
