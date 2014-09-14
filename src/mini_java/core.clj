(ns mini-java.core
  (:require [instaparse.core :as insta])
  (:gen-class))

(def interpreted-grammars
  {"add-mul"   (clojure.java.io/resource "resources/add-mul.instaparse"),
   "logic"     (clojure.java.io/resource "resources/logic.instaparse")})

(def compiled-grammars
  {"mini-java" (clojure.java.io/resource "resources/mini-java.instaparse")})

(def grammars
  (merge interpreted-grammars compiled-grammars))

(defn interpret
  [parser file]
  (with-open [f (clojure.java.io/reader (clojure.java.io/file file))]
    (doseq [line (line-seq f)]
      (-> line parser println))))

(defn compile
  [parser file]
  (-> file slurp parser println))

(defn -main
  "I don't do a whole lot ... yet."
  [grammar file & args]
  (let [parser (insta/parser (grammars grammar))]
    (cond
     (interpreted-grammars grammar) (interpret parser file)
     (compiled-grammars    grammar) (compile   parser file)
     :default                       (println "Error."))))
