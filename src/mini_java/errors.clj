(ns mini-java.errors
  (:require [mini-java.util :refer [parser-filename]]))

(defn underline-str [error-line index]
  "Given a line with an error at the given index,
  returns a string of whitespace ending with the ^ character, which
  points to the error. Tabs are handled correctly.

  For example, given the Java line:

  int[] foo ;= 2;

  returns
            ^"
  (let [whitespace (filter #(Character/isWhitespace %)
                           (take index error-line))
        remaining  (- index (count whitespace))]
    (str (apply str whitespace)
         (apply str (repeat remaining " "))
         "^")))

(defn underline-error [recognizer line column]
  (let [tokens     (.getInputStream recognizer)
        lines      (-> tokens
                       .getTokenSource .getInputStream .toString
                       (.split "\n"))]
    (if (> line (alength lines))
      ; reached EOF
      (do
        (println "<EOF>")
        (println "^"))
      ; did not reach EOF
      (let [error-line (aget lines (dec line))
            underline  (underline-str error-line column)]
        (println error-line)
        (println underline)))))

(defn print-error [parser msg line column]
  (let [filename (parser-filename parser)]
    (binding [*out* *err*]
      (println (str filename ":" line ": error: " msg))
      (underline-error parser line column))))

(defn- required-string [required]
  (str "  required: " required))

(defn- found-string [found]
  (str "  found:    " found))

(defn- symbol-string [symbol]
  (str "  symbol:   variable " symbol))

(defn- location-string [symbol]
  (str "  location: class FIXME"))

(def ^:private type-str-map
  {:int     "int",
   :boolean "boolean",
   :int<>   "int[]"})

(defn- type-str [type]
  "Returns the string representation of type"
  (get type-str-map type type))

(defn- arg-types-str [arg-types]
  (clojure.string/join "," (map type-str arg-types)))

(defn print-type-error [parser msg line column found required]
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (required-string (type-str required)))
    (println (found-string    (type-str found)))))

(defn print-symbol-error [parser msg line column symbol]
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (symbol-string symbol))
    (println (location-string symbol))))

(defn print-arg-types-error [parser msg line column given-types required-types]
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (required-string (arg-types-str required-types)))
    (println (found-string    (arg-types-str given-types)))))

(defn print-return-type-error [parser msg line column child-type parent-type]
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (required-string (type-str parent-type)))
    (println (found-string    (type-str child-type)))))
