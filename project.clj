(defproject mini-java "0.1.0-SNAPSHOT"
  :description "A MiniJava compiler implemented in Clojure."
  :url "https://github.com/dwysocki/mini-java"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [rhizome             "0.1.8"]]
  :plugins [[lein-antlr4 "0.1.0-SNAPSHOT"]]
  :hooks [leiningen.antlr4]
  :antlr-src-dir  "src/antlr"
  :antlr-dest-dir "src/java/antlr"
  :java-source-paths ["src/java/antlr/src/java/antlr"]
  :main mini-java.core)
