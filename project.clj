(defproject mini-java "0.2.0"
  :description "A MiniJava compiler implemented in Clojure."
  :url "https://github.com/dwysocki/mini-java"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure     "1.6.0"]
                 [org.antlr/antlr4        "4.3"  ]
                 [org.ow2.asm/asm         "5.0.3"]
                 [org.ow2.asm/asm-commons "5.0.3"]
                 [org.clojure/tools.cli   "0.3.1"]]
  :plugins [[lein-antlr4 "0.1.0-SNAPSHOT"]]
  :hooks [leiningen.antlr4]
  :antlr-src-dir  "src/antlr"
  :antlr-dest-dir ""
  :java-source-paths ["src/antlr"]
  :aot [mini-java.ErrorListener
        mini-java.ErrorHandler]
  :uberjar-name "mini-javac.jar"
  :main mini-java.core)
