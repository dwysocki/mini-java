(ns mini-java.parser
  (:require [mini-java.ast  :as ast]
            [mini-java.type-check :as type-check]
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
    (if (pos? errors)
      (println errors "errors occured.")
      (let [ast (ast/ast tree)
            class-table (type-check/class-table ast)]
        (println "AST:")
        (pprint ast)
        (println)
        (println "CLASS TABLE:")
        (pprint class-table)))))
