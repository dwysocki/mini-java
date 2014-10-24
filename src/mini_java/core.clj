(ns mini-java.core
  (:require [mini-java.parser           :as    parser]
            [mini-java.static-semantics :as    static-semantics]
            [clojure.tools.cli          :refer [parse-opts]]
            [clojure.pprint             :refer [pprint]])
  (:gen-class))

(def cli-options
  [[nil "--syntax-only"
    "Only do syntax checking"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: mini-javac [options] action"
        ""
        "Options:"
        options-summary]
       (clojure.string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn exit
  ([]
     (System/exit 0))
  ([status]
     (System/exit status))
  ([status msg]
     (println msg)
     (System/exit status)))

(defn -main
  ([& args]
     (let [{:keys [options arguments errors summary]}
           (parse-opts args cli-options)]
       (cond
        ; print help message
        (:help options) (exit 0 (usage summary))
        ; improper argument count
        (not= (count arguments) 1) (exit 1 (usage summary))
        ; errors in parsing command line options
        errors (exit 1 (error-msg errors)))

       (let [source-file (first arguments)
             ; parse AST from source file
             ast (parser/mini-java source-file)]
         ; exit if there are syntax errors
         (when (nil? ast) (exit 1 "Errors occurred."))
         ; exit if only syntax checking is requested
         (when (:syntax-only options) (exit 0))
         
         ; perform static semantics checking
         (let [class-table (static-semantics/class-table ast)]
           ; exit if there are semantic errors
           (when (nil? class-table) (exit 1 "Errors occurred."))

           (println "AST:")
           (pprint ast)
           (println)
           (println "CLASS TABLE:")
           (pprint class-table))))
     nil))
