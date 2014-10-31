(defproject mini-java "0.1.2"
  :description "A MiniJava compiler implemented in Clojure."
  :url "https://github.com/dwysocki/mini-java"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure   "1.6.0"]
                 [org.antlr/antlr4      "4.3"  ]
                 [org.clojure/tools.cli "0.3.1"]
                 [rhizome               "0.1.8"]]
  :plugins [[lein-antlr4 "0.1.0-SNAPSHOT"]]
  :hooks [leiningen.antlr4]
  :antlr-src-dir  "src/antlr"
  :antlr-dest-dir ""
  :antlr-options  {:visitor true}
  :java-source-paths ["src/antlr"]
  :aot [mini-java.ErrorListener
        mini-java.ErrorHandler]
  :uberjar-name "mini-javac.jar"
  :main mini-java.core)
