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

(defn- report-duplicate [[error-count parser] obj]
  (let [{:keys [context line column]} (meta obj)
        msg (str "duplicate " (context->type context) ": "
                 (:name obj))]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- report-shadow [[error-count parser] child var]
  (let [{:keys [line column]} (meta var)
        msg (str "class " (:name child) " "
                 "shadows variable " (:name var))]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- report-bad-type [[error-count parser] context found required]
  (let [{:keys [line column]} (meta context)
        msg (str "incompatible types")]
    (print-type-error parser msg line column found required))
  [(inc error-count) parser])

(defn- assert-type [found required context error-agent]
  (when (not= found required)
    (send-off report-bad-type error-agent context found required)))

(defn- info-map
  ([seq error-agent]
     (info-map seq {} error-agent))
  ([seq init error-agent]
     (-> (fn [r elem]
           (let [{:keys [:name] :as info} (info elem error-agent)]
             (if (get r name)
               (do
                 (send-off error-agent report-duplicate elem)
                 r)
               (assoc r name info))))
         (reduce init seq))))

(defmulti info (fn [x & args] (ast/context x)))

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
         :vars (info-map (:vars method) arg-vars error-agent)
         :body (:body method)}
      (with-meta (meta method)))))

(defmethod info :class-declaration [class error-agent]
  (-> {:name    (:name class),
       :parent  (:parent class),
       :vars    (info-map (:vars class) error-agent)
       :methods (info-map (:methods class) error-agent)}
    (with-meta (meta class))))

(defn- parent-seq [class class-table]
  "Returns a lazy seq of all parents of the given class."
  (lazy-seq
   (when-let [parent-name (:parent class)]
     (let [parent (class-table parent-name)]
       (cons parent (parent-seq parent class-table))))))

(defn- shadow-check [class parents error-agent]
  "Reports errors if class shadows any of its parent's fields."
  (when (seq parents)
    (let [parent-field-names
          (->> parents
               ;; create a seq of all vars of all parents
               (mapcat :vars)
               ;; take their names
               (map first)
               ;; remove duplicats
               set)]
      (doall
       (->> parent-field-names
            (map (:vars class))
            (filter identity)
            (map #(send-off error-agent report-shadow class %)))))))

(defn- override-check [class parents error-agent]
  "Reports errors if class overrides one of its parent's methods without
  using the same argument and return types."
  (when (seq parents)
    (let [parent-methods (mapcat :methods parents)]
      ;; TODO: actually finish this function      
      )))

(defmulti type-check (fn [x & args] (ast/context x)))

(defmethod type-check :default [node & args]
  node)

(defmethod type-check :class-declaration [class scopes error-agent]
  (let [scopes (assoc scopes :class class)]
    (doseq [method (vals (:methods class))]
      (type-check method scopes error-agent))))

(defmethod type-check :method-declaration [method scopes error-agent]
  (let [scopes (assoc scopes :method method)]
    (doseq [[_ statement] (:body method)]
      (type-check statement scopes error-agent))))

(defmethod type-check :if-else-statement [statement scopes error-agent]
  (let [pred (:pred statement)
        pred-type (type-check pred scopes error-agent)]
    (assert-type pred-type :boolean
                 pred error-agent))
  (type-check (:then statement))
  (type-check (:else statement)))



(defmethod type-check :and-expression [expression scopes error-agent]
  (let [left  (:left expression)
        right (:right expression)
        left-type (type-check left scopes error-agent)
        right-type (type-check right scopes error-agent)]
    (assert-type left-type :boolean
                 left error-agent)
    (assert-type right-type :boolean
                 right error-agent)
    :boolean))



(defmethod type-check :int-lit-expression [expression scopes error-agent]
  :int)

(defmethod type-check :boolean-expression [expression scopes error-agent]
  :boolean)

(defn class-table [ast parser]
  (let [error-agent (agent [0 parser])
        class-table (info-map (:classes ast) error-agent)
        classes     (vals class-table)]
    (doseq [class classes]
      (let [parents (parent-seq class class-table)
            scopes  {:parents parents}]
        (shadow-check   class parents error-agent)
        (override-check class parents error-agent)
        (type-check     class scopes  error-agent)))

    (await error-agent)
    (shutdown-agents)

    (when (zero? (first @error-agent))
      class-table)))
