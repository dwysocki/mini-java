(ns mini-java.static-semantics
  (:require [clojure.walk     :refer [walk]]
            [mini-java.ast    :as    ast]
            [mini-java.errors :refer [print-error]]))

(declare info)

(def ^:private context->type
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

(defn- unresolved-inheritance [[error-count parser] child-class]
  (let [{:keys [line column]} (meta child-class)
        msg (str "class " (:name child-class) " "
                 "inherits undefined class " (:parent child-class))]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- shadow-wrong-type [[error-count parser] child-var parent-var]
  (let [{:keys [line column]} (meta child-var)
        msg (str "variable " (:name child-var) " "
                 "with type " (:type child-var) " "
                 "shadows parent variable with type " (:type parent-var))]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- info-map
  ([seq error-agent]
     (info-map seq {} error-agent))
  ([seq init error-agent]
     (-> (fn [r elem]
           (let [{:keys [:name] :as info} (info elem error-agent)]
             (if (get r name)
               (do
                 (send-off error-agent duplicate elem)
                 r)
               (assoc r name info))))
         (reduce init seq))))

(defmulti info (fn [x y] (ast/context x)))

(defmethod info :default [obj error-agent]
  obj)

(defmethod info :var-declaration [var error-agent]
  (-> {:name (:name var),
       :type (:type var)}
    (with-meta (meta var))))

(defmethod info :method-declaration [method error-agent]
  (let [args (:args method)
        arg-vars (into {} (map (fn [v] [(:name v) v]) args))]
    (-> {:name (:name method),
         :type (:type method),
         :args args,
         :vars (info-map (:vars method) arg-vars error-agent)}
      (with-meta (meta method)))))

(defmethod info :class-declaration [class error-agent]
  (-> {:name    (:name class),
       :parent  (:parent class),
       :vars    (info-map (:vars class) error-agent)
       :methods (info-map (:methods class) error-agent)}
    (with-meta (meta class))))

#_(defn- inherit-var [child-vars parent-var error-agent]
  (let [var-name  (:name parent-var)
        child-var (child-vars var-name)]
    (cond
     ;; child does not shadow var, inherit it
     (not child-var)
     (assoc child-vars var-name parent-var)
     ;; child shadows var with a different type
     ;; don't inherit it, and report an error
     (not= (:type child-var) (:type parent-var))
     (do (send-off error-agent shadow-wrong-type child-var parent-var)
         child-vars)
     ;; child shadows var with the correct type
     ;; don't inherit it, and don't report an error
     :else child-vars)))

#_(defn- inherit [child parent error-agent]
  (let [child-vars  (:vars child)
        parent-vars (vals (:vars parent))]
    (assoc child
      :vars (reduce #(inherit-var %1 %2 error-agent)
                    child-vars parent-vars)
      :unresolved-inheritance? false)))

#_(defn- member-inheritance [class-map parent error-agent]
  (let [parent-name (:name parent)

        children (filter (comp (partial = parent-name)
                               :parent)
                         (vals class-map))]
    (-> (fn [r child]
          (let [name (:name child)
                inherited-child (inherit child parent error-agent)
                class-map (assoc r name inherited-child)]
            (member-inheritance class-map inherited-child error-agent)))
        (reduce class-map children))))

(defn class-table [ast parser]
  (let [error-agent (agent [0 parser])
        class-table (info-map (:classes ast) error-agent)
        ;; inherited-class-table (member-inheritance initial-class-table
        ;;                                           nil
        ;;                                           error-agent)
        ]
    #_(when-let [unresolved (->> inherited-class-table
                                 vals
                                 (filter :unresolved-inheritance?)
                                 seq)]
        (map #(send-off unresolved-inheritance error-agent %)
             unresolved))
    (await error-agent)
    (shutdown-agents)
    (when (zero? (first @error-agent))
      class-table)))
