(ns mini-java.static-semantics
  (:require [clojure.walk :refer [walk]]))

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
    :vars    (into {} (map var-info (:vars class))),
    :methods (into {} (map method-info (:methods class)))}])

(defn class-table [ast]
  (into {} (map class-info (:classes ast))))
