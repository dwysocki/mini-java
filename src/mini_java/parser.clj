(ns mini-java.parser
  (:import [mini_java.antlr
            MiniJavaParser MiniJavaLexer MiniJavaBaseListener]
           [org.antlr.v4.runtime
            ANTLRInputStream CommonTokenStream]
           [org.antlr.v4.runtime.tree
            ParseTree ParseTreeWalker]))

(defn mini-java [source-file]
  (let [input  (new ANTLRInputStream  source-file)
        lexer  (new MiniJavaLexer     input)
        tokens (new CommonTokenStream lexer)
        parser (new MiniJavaParser    tokens)]
    parser))


;; (def mini-java (antlr/parser "src/resources/MiniJava.g4"
;;                              {:throw? false}))

;; (def errors
;;   (comp :errors meta))
