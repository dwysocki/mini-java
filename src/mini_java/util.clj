(ns mini-java.util
  (:require clojure.set))

(defn parser-filename [parser]
  (-> parser
      .getInputStream
      .getSourceName))

(defn token-line-and-column [token]
  "Returns the line and column of the given token in the form [line column]."
  (let [line   (.getLine token)
        column (.getCharPositionInLine token)]
    [line column]))

(defn camel->lisp [s]
  (-> s
      (clojure.string/replace #"([a-z])([A-Z])" "$1-$2")
      clojure.string/lower-case))

(defn two-way-set-difference [s1 s2]
  (clojure.set/union
    (clojure.set/difference s1 s2)
    (clojure.set/difference s2 s1)))
