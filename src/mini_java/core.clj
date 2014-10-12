(ns mini-java.core
  (:require [clj-antlr.core  :as antlr]
          ; [instaparse.core :refer [visualize]]
            [clojure.pprint  :refer [pprint]])
  (:gen-class))

(def mini-java (antlr/parser "src/resources/MiniJava.g4"
                             {:throw? false}))


(defn -main
  ([source-file & args]
     (let [source-file-stream (slurp source-file)
           ast                (mini-java source-file-stream)
           errors             (-> ast meta :errors)]
       (when (seq errors) (pprint errors))
       (pprint ast))))


;; (ns mini-java.core
;;   (:require [instaparse.core :as insta]
;;             [clojure.edn])
;;   (:gen-class))

;; (def trans
;;   {:int-lit clojure.edn/read-string,
;;    :true    (constantly true),
;;    :false   (constantly false)})

;; (defn -main
;;   ([source-file & args]
;;      (let [grammar (clojure.java.io/resource "resources/mini-java.instaparse")
;;            parser  (insta/parser grammar
;;                                  :auto-whitespace :standard
;;                                  :output-format   :enlive)
;;            ast     (insta/parse parser (slurp source-file)
;;                                 :total true)
;;            ast     (insta/transform trans ast)]
;;        (when (insta/failure? ast) (println (insta/get-failure ast)))
;;        (insta/visualize ast))))
