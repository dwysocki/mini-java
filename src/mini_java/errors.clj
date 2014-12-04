(ns mini-java.errors
  "Utility functions for printing errors.
  Used in both parse errors and static semantics errors."
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
    (str (clojure.string/join whitespace)
         (clojure.string/join (repeat remaining " "))
         "^")))

(defn underline-error [parser line column]
  "Prints the line on which the current error occurred, and underlines
  the error with a ^"
  (let [tokens     (.getInputStream parser)
        lines      (-> tokens
                       .getTokenSource .getInputStream .toString
                       (clojure.string/split "\n"))]
    (if (> line (alength lines))
      ; reached EOF, underline that
      (println "<EOF>\n^")
      ; did not reach EOF, do a more descriptive underline
      (let [error-line (aget lines (dec line))
            underline  (underline-str error-line column)]
        (println error-line)
        (println underline)))))

(defn print-error [parser msg line column]
  "Prints the given error msg along with the file, line, and column in which
  it occurred. This is used for _all_ errors."
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

(defn- location-string [class]
  (str "  location: class " class))

(def ^:private type-str-map
  {:int     "int",
   :boolean "boolean",
   :int<>   "int[]"})

(defn- type-str [type]
  "Returns the string representation of type"
  (get type-str-map type type))

(defn- arg-types-str [arg-types]
  "Returns the string representation of a list of argument types"
  (clojure.string/join "," (map type-str arg-types)))

(defn print-type-error [parser msg line column found required]
  "Prints a type mismatch error"
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (required-string (type-str required)))
    (println (found-string    (type-str found)))))

(defn print-symbol-error [parser msg line column symbol scopes]
  "Prints an unknown symbol error"
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (symbol-string symbol))
    (println (location-string (-> scopes :class :name)))))

(defn print-arg-types-error [parser msg line column given-types required-types]
  "Prints an argument type mismatch error"
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (required-string (arg-types-str required-types)))
    (println (found-string    (arg-types-str given-types)))))

(defn print-return-type-error [parser msg line column child-type parent-type]
  "Prints a return type mismatch error"
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println (required-string (type-str parent-type)))
    (println (found-string    (type-str child-type)))))
