(defproject mini-java "0.1.0-SNAPSHOT"
  :description "A MiniJava compiler implemented in Clojure."
  :url "https://github.com/dwysocki/mini-java"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-antlr           "0.2.2"]
                 [instaparse          "1.3.4"]
                 [rhizome             "0.1.8"]]
  :main mini-java.core)
