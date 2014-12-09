(ns mini-java.errors
  "Utility functions for printing errors.
  Used in both parse errors and static semantics errors."
  (:require [mini-java.util :refer [parser-filename]]))

(defn underline-str
  "Given a line with an error at the given index,
  returns a string of whitespace ending with the ^ character, which
  points to the error. Tabs are handled correctly.

  For example, given the Java line:

  int[] foo ;= 2;

  returns
            ^"
  [error-line index]
  (let [whitespace (filter #(Character/isWhitespace %)
                           (take index error-line))
        remaining  (- index (count whitespace))]
    (str (clojure.string/join whitespace)
         (clojure.string/join (repeat remaining " "))
         "^")))

(defn underline-error
  "Prints the line on which the current error occurred, and underlines
  the error with a ^"
  [parser line column]
  (let [tokens     (.getInputStream parser)
        lines      (-> tokens
                       .getTokenSource .getInputStream .toString
                       clojure.string/split-lines)]
    (if (> line (count lines))
      ; reached EOF, underline that
      (println "<EOF>\n^")
      ; did not reach EOF, do a more descriptive underline
      (let [error-line (nth lines (dec line))
            underline  (underline-str error-line column)]
        (println error-line)
        (println underline)))))

(defn print-error
  "Prints the given error msg along with the file, line, and column in which
  it occurred. This is used for _all_ errors."
  [parser msg line column]
  (let [filename (parser-filename parser)]
    (binding [*out* *err*]
      (println (str filename ":" line ": error: " msg))
      (underline-error parser line column))))

(defn print-type-error
  "Prints a type mismatch error"
  [parser msg line column found required]
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println "  required:" required)
    (println "  found:   " found)))

(defn print-symbol-error
  "Prints a missing symbol error"
  [parser msg line column symbol location]
  (print-error parser msg line column)
  (binding [*out* *err*]
    (println "  symbol:   variable" symbol)
    (println "  location: class"    location)))
