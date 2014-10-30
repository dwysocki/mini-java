(ns mini-java.static-semantics
  (:require [clojure.walk     :refer [walk]]
            [mini-java.ast    :as    ast]
            [mini-java.errors :refer [print-error
                                      print-type-error
                                      print-symbol-error]]))

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
        msg "incompatible types"]
    (print-type-error parser msg line column found required))
  [(inc error-count) parser])

(defn- report-missing-symbol [[error-count parser] context]
  (let [{:keys [line column]} (meta context)
        symbol (:id context)
        msg "cannot find symbol"]
    (print-symbol-error parser msg line column symbol))
  [(inc error-count) parser])

(defn- assert-type [found required context error-agent]
  (when (not= found required)
    (send-off error-agent report-bad-type context found required)))

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

(defn- get-var [id scopes]
  (or (-> scopes :method :vars (get id))
      (-> scopes :class  :vars (get id))))

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
  nil)

(defmethod type-check :class-declaration [class scopes error-agent]
  (let [scopes (assoc scopes :class class)]
    (doseq [method (vals (:methods class))]
      (type-check method scopes error-agent))))

(defmethod type-check :method-declaration [method scopes error-agent]
  (let [scopes (assoc scopes :method method)]
    (doseq [statement (:body method)]
      (type-check statement scopes error-agent))))

(defmethod type-check :nested-statement [statements scopes error-agent]
  (doseq [statement statements]
    (type-check statement scopes error-agent)))

(defmethod type-check :if-else-statement [statement scopes error-agent]
  (let [pred (:pred statement)
        pred-type (type-check pred scopes error-agent)]
    (assert-type pred-type :boolean
                 pred error-agent))
  (type-check (:then statement) scopes error-agent)
  (type-check (:else statement) scopes error-agent))

(defmethod type-check :while-statement [statement scopes error-agent]
  (let [pred (:pred statement)
        pred-type (type-check pred scopes error-agent)]
    (assert-type pred-type :boolean
                 pred error-agent))
  (type-check (:body statement) scopes error-agent))

(defmethod type-check :print-statement [statement scopes error-agent]
  "Check that print statement has an int as its argument."
  (let [arg (:arg statement)
        arg-type (type-check arg scopes error-agent)]
    (assert-type arg-type :int
                 arg error-agent)))

(defmethod type-check :assign-statement [statement scopes error-agent]
  (let [target (:target statement)
        source (:source statement)
        target-type (:type (get-var target scopes))
        source-type (type-check source scopes error-agent)]
    (if target-type
      (assert-type source-type target-type
                   source error-agent)
      (send-off error-agent report-missing-symbol statement))))

(defmethod type-check :array-assign-statement [statement scopes error-agent]
  ;; TODO
  ;; check that ID refers to a valid :int<>, index is :int, and value is :int
  )

(defmethod type-check :return-statement [statement scopes error-agent]
  ;; TODO
  ;; check that return type equals method's return type
  )

(defmethod type-check :recur-statement [statement scopes error-agent]
  ;; TODO
  ;; check that base case's type equals method's return type
  ;; predicate is boolean,
  ;; and argument list matches method's
  )

(defn- binary-op-type-check [expression type scopes error-agent]
  (let [left  (:left expression)
        right (:right expression)
        left-type (type-check left scopes error-agent)
        right-type (type-check right scopes error-agent)]
    (assert-type left-type type
                 left error-agent)
    (assert-type right-type type
                 right error-agent)))

(defmethod type-check :and-expression [expression scopes error-agent]
  (binary-op-type-check expression :boolean scopes error-agent)
  :boolean)

(defmethod type-check :lt-expression [expression scopes error-agent]
  (binary-op-type-check expression :int scopes error-agent)
  :boolean)

(defmethod type-check :add-expression [expression scopes error-agent]
  (binary-op-type-check expression :int scopes error-agent)
  :int)

(defmethod type-check :sub-expression [expression scopes error-agent]
  (binary-op-type-check expression :int scopes error-agent)
  :int)

(defmethod type-check :mul-expression [expression scopes error-agent]
  (binary-op-type-check expression :int scopes error-agent)
  :int)

(defmethod type-check :array-access-expression [expression scopes error-agent]
  ;; TODO
  :int)

(defmethod type-check :array-length-expression [expression scopes error-agent]
  ;; TODO
  :int)

(defmethod type-check :int-lit-expression [expression scopes error-agent]
  :int)

(defmethod type-check :boolean-expression [expression scopes error-agent]
  :boolean)

(defmethod type-check :identifier-expression [expression scopes error-agent]
  "Returns the type of the variable which the identifier is bound to.
  If the variable does not exist, reports and error and returns nil."
  (or (:type (get-var (:id expression) scopes))
      (do (send-off error-agent report-missing-symbol expression)
          nil)))



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
