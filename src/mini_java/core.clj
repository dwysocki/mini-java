(ns mini-java.core
  (:require [mini-java.parser :as    parser]
            [rhizome.viz      :as    rhizome]
            [clojure.pprint   :refer [pprint]])
  (:gen-class))

(defn -main
  ([source-file & args]
     (println (parser/mini-java source-file))))
