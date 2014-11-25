(ns mini-java.core
  (:require [mini-java.parser           :as    parser]
            [mini-java.static-semantics :as    static-semantics]
            [mini-java.code-gen         :as    code-gen]
            [clojure.tools.cli          :refer [parse-opts]]
            [clojure.pprint             :refer [pprint]])
  (:gen-class))

(def cli-options
  [["-d" "--directory DIR" "Destination directory for class files"
    :default "."]
   [nil "--syntax"
    "Stop after syntax checking"]
   [nil "--static-semantics"
    "Stop after static semantics checking"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: mini-javac [options] filename"
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
        ;; print help message
        (:help options) (exit 0 (usage summary))
        ;; improper argument count
        (not= (count arguments) 1) (exit 1 (usage summary))
        ;; errors in parsing command line options
        errors (exit 1 (error-msg errors)))

       (let [source-file (first arguments)
             ;; parse AST from source file
             [ast parser] (parser/mini-java source-file)]
         ;; exit if there are syntax errors
         (when (nil? ast)
           (exit 1 "Errors occurred."))
         ;; exit if only syntax checking is requested
         (when (:syntax options)
           (exit 0))
         
         ;; perform static semantics checking
         (let [[class-table errors]
               (static-semantics/class-table ast parser)]
           ;; exit if there are semantic errors
           (when-not (zero? errors)
             (exit 1 (str errors " errors occurred.")))
           ;; exit if only static semantics checking is requested
           (when (:static-semantics options)
             (pprint class-table)
             (exit 0))

           (code-gen/write-classes class-table (:directory options)))))
     nil))
