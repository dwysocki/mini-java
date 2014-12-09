(ns mini-java.util
  "Miscellaneous utility functions."
  (:require [clojure.set :as set]))

(defn parser-filename
  "Given an ANTLR parser, returns the name of the file it is parsing."
  [parser]
  (-> parser
      .getInputStream
      .getSourceName))

(defn token-line-and-column
  "Returns the line and column of the given token in the form [line column]."
  [token]
  (let [line   (.getLine token)
        column (.getCharPositionInLine token)]
    [line column]))

(defn camel->lisp
  "Converts CamelCase to lisp-case."
  [s]
  (-> s
      (clojure.string/replace #"([a-z])([A-Z])" "$1-$2")
      clojure.string/lower-case))

(defn symmetric-set-difference
  "Given two sets, returns all of the elements which are only contained in a
  single set.

  This is done by the set operation: (s1 - s2) ∪ (s2 - s1)."
  [s1 s2]
  (set/union (set/difference s1 s2)
             (set/difference s2 s1)))
