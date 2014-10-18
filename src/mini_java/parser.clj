(ns mini-java.parser
  (:import [mini_java.antlr
            MiniJavaParser MiniJavaLexer MiniJavaBaseListener]
           [org.antlr.v4.runtime
            ANTLRInputStream CommonTokenStream]
           [org.antlr.v4.runtime.tree
            ParseTree ParseTreeWalker]))

(defn- make-listener []
  (proxy [MiniJavaBaseListener] []
    (enterExpression [ctx] (println "EXPRESSION!" ctx))))


(defn mini-java [source-file]
  (let [stream (clojure.java.io/input-stream source-file)
        input  (new ANTLRInputStream  stream)
        lexer  (new MiniJavaLexer     input)
        tokens (new CommonTokenStream lexer)
        parser (new MiniJavaParser    tokens)
        tree   (.goal parser)]
    tree))


;; (def mini-java (antlr/parser "src/resources/MiniJava.g4"
;;                              {:throw? false}))

;; (def errors
;;   (comp :errors meta))
