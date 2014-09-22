(ns mini-java.core
  (:require [instaparse.core :as insta])
  (:gen-class))

(defn -main
  [source-file & args]
  (let [grammar (clojure.java.io/resource "resources/mini-java.instaparse")
        parser  (insta/parser grammar :auto-whitespace :standard)
        ast     (parser (slurp source-file))]
    (if-not (insta/failure? ast)
      (insta/visualize ast)
      ast)))
