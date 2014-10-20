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
      (println (str filename ":" line ": " msg))
      (underline-error parser line column))))

