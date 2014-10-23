(ns mini-java.util)

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
