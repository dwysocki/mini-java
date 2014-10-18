(ns mini-java.core
  (:require [mini-java.parser :as    parser]
            [rhizome.viz      :as    rhizome]
            [clojure.reflect  :as    r]
            [clojure.pprint   :refer [pprint print-table]])
  (:gen-class))

(defn -main
  ([source-file & args]
     (parser/mini-java source-file)))
