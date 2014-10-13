(ns mini-java.parser
  (:require [clj-antlr.core :as antlr]))

(def mini-java (antlr/parser "src/resources/MiniJava.g4"
                             {:throw? false}))

(def errors
  (comp :errors meta))
