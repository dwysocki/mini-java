(ns mini-java.ErrorListener
  "Extends ANTLR's BaseErrorListener, in order to implement custom displays of
  parse errors. All error reporting displays the line and column number,
  and underlines the place in the line where the error occurred."
  (:require [mini-java.errors :refer [print-error]])
  (:gen-class
     :name    mini-java.ErrorListener
     :extends org.antlr.v4.runtime.BaseErrorListener))

(defn -syntaxError [this parser symbol line column msg exeption]
  "Report a syntax error."
  (print-error parser msg line column))
