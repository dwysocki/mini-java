(ns mini-java.parser
  "Uses ANTLR to parse a MiniJava source file into an AST.
  ANTLR outputs a data structure which is not ideal, and so it is passed to
  mini-java.ast/ast, which transforms it into a more idealized AST."
  (:require [mini-java.ast  :as ast]
            [clojure.pprint :refer [pprint]])
  (:import [mini-java
            ErrorHandler ErrorListener]
           [mini_java.antlr
            MiniJavaLexer MiniJavaParser]
           [org.antlr.v4.runtime
            ANTLRFileStream CommonTokenStream]))

(defn mini-java
  "Parse the given source file using ANTLR, and output a minimal hash-map
  representation of an AST."
  [source-file]
  (let [input  (new ANTLRFileStream   source-file)
        lexer  (new MiniJavaLexer     input)
        tokens (new CommonTokenStream lexer)
        ;; create parser with custom error listener and error handler
        parser (doto (new MiniJavaParser tokens)
                 (.removeErrorListeners)
                 (.addErrorListener (new ErrorListener))
                 (.setErrorHandler  (new ErrorHandler)))
        ;; parse file
        tree   (.goal parser)
        errors (.getNumberOfSyntaxErrors parser)]
    [(ast/ast tree) parser errors]))
