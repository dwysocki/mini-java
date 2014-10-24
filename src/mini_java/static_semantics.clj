(ns mini-java.static-semantics
  (:require [clojure.walk :refer [walk]]))

(defn- initial [type]
  (case type
    :int 0
    :boolean false
    nil))

(defn- initialize
  [[k v]]
  (let [type (:type v)
        init (initial type)]
    [k, (assoc v :val init)]))

(defn- var-info [var]
  [(:name var),
   {:name (:name var),
    :type (:type var)}])

(defn- method-info [method]
  [(:name method),
   {:name (:name method),
    :type (:type method),
    :args (:args method),
    :vars (into {} (map var-info (:vars method)))}])

(defn- class-info [class]
  [(:name class),
   {:name    (:name class),
    :parent  (:parent class),
    :vars    (into {} (map (comp initialize var-info) (:vars class))),
    :methods (into {} (map method-info (:methods class)))}])

(defn- make-class-table [ast error-agent]
  "Makes the class table, reporting any errors to the error-agent.

  Do not use into, as it allows overwriting."
  (into {} (map class-info (:classes ast))))

(defn class-table [ast]
  (let [error-agent (agent 0)
        class-table (make-class-table ast error-agent)]
    (await error-agent)
    (shutdown-agents)
    (if (zero? @error-agent)
      class-table
      nil)))
