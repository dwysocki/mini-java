(ns mini-java.parser
  (:import [mini-java
            ErrorHandler ErrorListener MiniJavaParser]
           [mini_java.antlr
            MiniJavaLexer MiniJavaBaseListener]
           [org.antlr.v4.runtime
            ANTLRFileStream CommonTokenStream]
           [org.antlr.v4.runtime.tree
            ParseTree ParseTreeWalker]))

(defn mini-java [source-file]
  (let [input  (new ANTLRFileStream   source-file)
        lexer  (new MiniJavaLexer     input)
        tokens (new CommonTokenStream lexer)
        parser (doto (new MiniJavaParser tokens)
                 (.removeErrorListeners)
                 (.addErrorListener (new ErrorListener))
                 (.setErrorHandler  (new ErrorHandler)))
        tree   (.goal parser)]
    tree))
