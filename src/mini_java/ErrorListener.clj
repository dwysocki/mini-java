(ns mini-java.ErrorListener
  (:require [mini-java.errors :refer [print-error]])
  (:gen-class
     :name    mini-java.ErrorListener
     :extends org.antlr.v4.runtime.BaseErrorListener))

(defn -syntaxError [this parser symbol line column msg exeption]
  (print-error parser msg line column))
