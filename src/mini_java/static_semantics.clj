(ns mini-java.static-semantics
  (:require [clojure.walk :refer [walk]]
            [mini-java.ast    :as    ast]
            [mini-java.errors :refer [print-error]]))

(declare info)

(def ^{:private true} context->type
  {:method-declaration "method",
   :class-declaration  "class",
   :var-declaration    "variable"})

(defn- initial [type]
  (case type
    :int 0
    :boolean false
    nil))

(defn- initialize [[k v]]
  (let [type (:type v)
        init (initial type)]
    [k, (assoc v :val init)]))

(defn- duplicate [[error-count parser] obj]
  (let [{:keys [context line column]} (meta obj)
        msg (str "duplicate " (context->type context) ": "
                 (:name obj))]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- info-map
  ([seq error-agent]
     (info-map seq {} error-agent))
  ([seq init error-agent]
     (-> (fn [r elem]
           (let [{:keys [:name] :as info} (info elem error-agent)]
             (if (r name)
               (do
                 (send-off error-agent duplicate elem)
                 r)
               (assoc r name info))))
         (reduce init seq))))

(defmulti info (fn [x y] (ast/context x)))

(defmethod info :default [obj error-agent]
  obj)

(defmethod info :var-declaration [var error-agent]
  ^{:context :var-declaration}
  {:name (:name var),
   :type (:type var)})

(defmethod info :method-declaration [method error-agent]
  (let [args (:args method)
        vars (into {} (map (fn [v] [(:name v) v]) args))]
   ^{:context :method-declaration}
   {:name (:name method),
    :type (:type method),
    :args args,
    :vars (info-map (:vars method) vars error-agent)
    }))

(defmethod info :class-declaration [class error-agent]
  ^{:context :class-declaration}
  {:name    (:name class),
   :parent  (:parent class),
   :vars    (info-map (:vars class) error-agent)
   :methods (info-map (:methods class) error-agent)})

(defn class-table [ast parser]
  (let [error-agent (agent [0 parser])
        class-table (info-map (:classes ast) error-agent)]
    (println (agent-error error-agent))
    (await error-agent)
    (shutdown-agents)
    (if (zero? (first @error-agent))
      class-table
      nil)))
