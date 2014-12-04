(ns mini-java.core
  (:require [mini-java.parser           :as    parser]
            [mini-java.static-semantics :as    static-semantics]
            [mini-java.code-gen         :as    code-gen]
            [clojure.tools.cli          :refer [parse-opts]])
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
  "Formats cli usage summary message."
  (->> ["Usage: mini-javac [options] filename"
        ""
        "Options:"
        options-summary]
       (clojure.string/join \newline)))

(defn error-msg [errors]
  "Formats cli parse error message."
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn exit
  "Exits the program, with an optional exit status and message.
  Default exit status is 0."
  ([]
     (System/exit 0))
  ([status]
     (System/exit status))
  ([status msg]
     (println msg)
     (System/exit status)))

(defn errors-occured [n]
  (str n " error"
       (if (= 1 n) "" "s")
       " occurred."))

(defn -main
  "Parse the command line arguments and perform the compilation."
  [& args]
  (let [{:keys [options arguments errors summary]}
        (parse-opts args cli-options)]
    ;; check for anything which might cause the program to exit before
    ;; parsing the source file
    (cond
     ;; print help message
     (:help options) (exit 0 (usage summary))
     ;; only one positional argument is expected
     (not= (count arguments) 1) (exit 1 (usage summary))
     ;; errors in parsing command line options
     errors (exit 1 (error-msg errors)))

    ;; begin compilation process
    (let [source-file (first arguments)
          ;; parse AST from source file
          [ast parser errors] (parser/mini-java source-file)]
      ;; exit if there are syntax errors
      (when (pos? errors)
        (exit 1 (errors-occured errors)))
      ;; exit if only syntax checking is requested
      (when (:syntax options)
        (exit 0))
      
      ;; perform static semantics checking
      (let [[class-table errors]
            (static-semantics/class-table ast parser)]
        ;; exit if there are semantic errors
        (when-not (zero? errors)
          (exit 1 (errors-occured errors)))
        ;; exit if only static semantics checking is requested
        (when (:static-semantics options)
          (exit 0))
        ;; generate bytecode and write to files in the given directory
        (code-gen/write-classes class-table
                                (:directory options)))))
  nil)
