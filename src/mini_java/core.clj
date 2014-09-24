(ns mini-java.core
  (:require [instaparse.core :as insta]
            [clojure.edn])
  (:gen-class))

(def trans
  {:IntegerLiteral clojure.edn/read-string})

(defn -main
  ([source-file & args]
     (let [grammar (clojure.java.io/resource "resources/mini-java.instaparse")
           parser  (insta/parser grammar :auto-whitespace :standard)
           ast     (parser (slurp source-file)
                           :total true)
           ast     (insta/transform trans ast)]
       (insta/visualize ast))))
