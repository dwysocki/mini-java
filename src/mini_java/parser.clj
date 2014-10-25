(ns mini-java.parser
  (:require [mini-java.ast  :as ast]
            [clojure.pprint :refer [pprint]])
  (:import [mini-java
            ErrorHandler ErrorListener]
           [mini_java.antlr
            MiniJavaLexer MiniJavaParser]
           [org.antlr.v4.runtime
            ANTLRFileStream CommonTokenStream]))

(defn mini-java [source-file]
  (let [input  (new ANTLRFileStream   source-file)
        lexer  (new MiniJavaLexer     input)
        tokens (new CommonTokenStream lexer)
        parser (doto (new MiniJavaParser tokens)
                 (.removeErrorListeners)
                 (.addErrorListener (new ErrorListener))
                 (.setErrorHandler  (new ErrorHandler)))
        tree   (.goal parser)
        errors (.getNumberOfSyntaxErrors parser)]
    (when-not (pos? errors)
      [(ast/ast tree) parser])))
