(ns mini-java.static-semantics
  (:require [clojure.walk     :refer [walk]]
            [mini-java.ast    :as    ast]
            [mini-java.errors :refer [print-error
                                      print-type-error
                                      print-symbol-error
                                      print-arg-types-error
                                      print-return-type-error]]))

(declare info type-check parent-seq)

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

(defn- report-cyclic-inheritance [[error-count parser] class]
  (let [{:keys [line column]} (meta class)
        msg (str "cyclic inheritance involving " (:name class))]
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

(defn- report-missing-method [[error-count parser] context method-name]
  (let [{:keys [line column]} (meta context)
        msg (str "cannot find method " method-name)]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- report-missing-type [[error-count parser] context type]
  (let [{:keys [line column]} (meta context)
        msg (str "cannot find type " type)]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- report-number-of-args [[error-count parser] context n-required]
  (let [{:keys [line column]} (meta context)
        msg (str "wrong number of args given (" n-required " required)")]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- report-type-args [[error-count parser]
                         given-types required-types context]
  (let [{:keys [line column]} (meta context)
        msg "method cannot be applied to given types"]
    (print-arg-types-error parser msg line column given-types required-types))
  [(inc error-count) parser])

(defn- report-overload [[error-count parser] context child-type parent-type]
  (let [{:keys [line column]} (meta context)
        msg (str "method " (:name context) " overloads parent method")]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- report-return-type [[error-count parser] context child-type parent-type]
  (let [{:keys [line column]} (meta context)
        msg (str "method " (:name context) " "
                 "overrides parent method with wrong type")]
    (print-return-type-error parser msg line column child-type parent-type))
  [(inc error-count) parser])

(defn- report-no-return [[error-count parser] method]
  (let [{:keys [line column]} (meta method)
        msg (str "method " (:name method) " does not return")]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- report-non-tail-return [[error-count parser] statement]
  (let [{:keys [line column context]} (meta statement)
        statement-type (if (= context :return-statement)
                         "return"
                         "recur")
        msg (str statement-type " only allowed from tail position of method")]
    (print-error parser msg line column))
  [(inc error-count) parser])

(defn- subtype? [found required class-table]
  (or (= found required)
      (let [found-class (class-table found)]
        (when-let [parents (parent-seq found-class class-table)]
          (some (partial = required)
                (map :name parents))))))

(defn- assert-type [found required context scopes error-agent]
  (when-not (or (nil? found)
                (nil? required))
    (when-not (subtype? found required (:class-table scopes))
      (send-off error-agent report-bad-type context found required))))

(defn- assert-type-exists [type class-table context error-agent]
  (when-not (or (class-table type)
                (ast/primitives type))
    (send-off error-agent report-missing-type context type)))

(defn- info-map
  ([seq error-agent]
     (info-map seq {} error-agent))
  ([seq init error-agent]
     (-> (fn [r elem]
           (let [{:keys [name] :as info} (info elem error-agent)]
             (if (get r name)
               (do (send-off error-agent report-duplicate elem)
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

(defmethod info :main-class-declaration [class error-agent]
  (-> {:name    (:name class)
       :main?   true
       :vars    ()
       :methods {:main {:name "main"
                        :vars ()
                        :args ()
                        :body (:body class)}}}
    (with-meta (meta class))))

(defn parent-seq [class class-table]
  "Returns a lazy seq of all parents of the given class."
  (lazy-seq
   (when-let [parent-name (:parent class)]
     (let [parent (class-table parent-name)]
       (cons parent (parent-seq parent class-table))))))

(defn locate-var [id scopes]
  (or (-> scopes :method :vars (get id))
      (-> scopes :class  :vars (get id))
      (loop [parents (:parents scopes)]
        (when (seq parents)
         (let [parent (first parents)
               vars   (:vars parent)]
           (if-let [var (get vars id)]
             var
             (recur (next parents))))))))

(defn locate-method [class method-name scopes]
  "Returns the method bound to method-name by traversing class'
  inheritance chain until it finds an implementation of the method."
  (if-let [method (get (:methods class) method-name)]
    ;; return the method if found
    method
    (when-let [parent ((:class-table scopes) (:parent class))]
      ;; recur on parent class if method not found
      ;; or return nil if parent is nil
      (recur parent method-name scopes))))

(defn- check-arg-count [given-args required-args error-agent]
  (let [n-given    (count given-args)
        n-required (count required-args)]
    (if (= n-given n-required)
      ;; correct number of arguments given
      true
      ;; wrong number of arguments given
      (send-off error-agent report-number-of-args given-args n-required))))

(defn- check-arg-types [given-args required-args scopes error-agent]
  (let [given-types (map #(type-check % scopes error-agent) given-args)
        required-types (map :type required-args)]
    (if (every? identity (map #(subtype? %1 %2 (:class-table scopes))
                           given-types required-types))
      ;; all arguments are of required type
      true
      ;; not all arguments are of required type, report it
      (send-off error-agent
                report-type-args given-types required-types given-args))))

(defn- check-args [given-args required-args context scopes error-agent]
  (and (check-arg-count given-args required-args error-agent)
       (check-arg-types given-args required-args scopes error-agent)))

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

(defn- override-check [child-methods parents error-agent]
  "Reports errors if class overrides one of its parent's methods without
  using the same argument and return types."
  (when-let [parent (first parents)]
    (let [parent-methods         (:methods parent)
          child-method-names     (set (keys child-methods))
          parent-method-names    (set (keys parent-methods))
          overriden-method-names (clojure.set/intersection child-method-names
                                                           parent-method-names)
          methods
          (-> (fn [methods name]
                (let [child-method  (child-methods name)
                      parent-method (parent-methods name)
                      child-type    (:type child-method)
                      parent-type   (:type parent-method)
                      child-types   (->> child-method  :args (map :type))
                      parent-types  (->> parent-method :args (map :type))]
                  (cond
                   ;; report overloading
                   (not= child-types parent-types)
                   (send-off error-agent report-overload
                             child-method child-types parent-types)

                   ;; report override with different return type
                   (not= child-type parent-type)
                   (send-off error-agent report-return-type
                             child-method child-type parent-type)))
                ;; remove method from methods, so as not to repeat multiple
                ;; errors for the same method override
                (dissoc methods name))
              (reduce child-methods overriden-method-names))]
      (recur methods (rest parents) error-agent))))



(defmulti type-check (fn [x & args] (ast/context x)))

(defmethod type-check :default [node & args]
  (cond
   (= node :this)
   (let [[scopes error-agent] args
         this-class (:class scopes)]
     (:name this-class))))

(defmethod type-check :class-declaration [class scopes error-agent]
  (let [scopes (assoc scopes :class class)]
    (doseq [var (vals (:vars class))]
      (assert-type-exists (:type var) (:class-table scopes) var error-agent))
    (doseq [method (vals (:methods class))]
      (type-check method scopes error-agent))))

(defmethod type-check :method-declaration [method scopes error-agent]
  (let [scopes (assoc scopes :method method)]
    (doseq [var (vals (:vars class))]
      (assert-type-exists (:type var) (:class-table scopes) var error-agent))
    (let [statements (:body method)]
      ;; type check statements except for last one
      (doseq [statement (butlast statements)]
        (type-check statement scopes error-agent))
      ;; check that last statement is a return statement
      (let [final-statement (last statements)
            final-statement-type (ast/context final-statement)]
        (when-not (or (= final-statement-type :return-statement)
                      (= final-statement-type :recur-statement))
          (send-off error-agent report-no-return method))
        (type-check final-statement (assoc scopes :tail? true) error-agent)))))

(defmethod type-check :nested-statement [statements scopes error-agent]
  (doseq [statement statements]
    (type-check statement scopes error-agent)))

(defmethod type-check :if-else-statement [statement scopes error-agent]
  (let [pred (:pred statement)
        pred-type (type-check pred scopes error-agent)]
    (assert-type pred-type :boolean
                 pred scopes error-agent))
  (type-check (:then statement) scopes error-agent)
  (type-check (:else statement) scopes error-agent))

(defmethod type-check :while-statement [statement scopes error-agent]
  (let [pred (:pred statement)
        pred-type (type-check pred scopes error-agent)]
    (assert-type pred-type :boolean
                 pred scopes error-agent))
  (type-check (:body statement) scopes error-agent))

(defmethod type-check :print-statement [statement scopes error-agent]
  "Check that print statement has an int as its argument."
  (let [arg (:arg statement)
        arg-type (type-check arg scopes error-agent)]
    (assert-type arg-type :int
                 arg scopes error-agent)))

(defmethod type-check :assign-statement [statement scopes error-agent]
  (let [target (:target statement)
        source (:source statement)
        target-type (:type (locate-var target scopes))
        source-type (type-check source scopes error-agent)]
    (if target-type
      (assert-type source-type target-type
                   source scopes error-agent)
      (send-off error-agent report-missing-symbol statement))))

(defmethod type-check :array-assign-statement [statement scopes error-agent]
  (let [{:keys [target index source]} statement
        target-type (:type (locate-var target scopes))
        index-type  (type-check index scopes error-agent)
        source-type (type-check source scopes error-agent)]
    (assert-type target-type :int<>
                 target scopes error-agent)
    (assert-type index-type :int
                 index scopes error-agent)
    (assert-type source-type :int
                 source scopes error-agent)))

(defmethod type-check :return-statement [statement scopes error-agent]
  (let [return-value (:return-value statement)
        method-return-type (:type (:method scopes))
        return-value-type (type-check return-value scopes error-agent)]
    ;; check that return type matches method's return type
    (assert-type return-value-type method-return-type
                 return-value scopes error-agent)
    ;; check that return is from tail position
    (when-not (:tail? scopes)
      (send-off error-agent report-non-tail-return statement))))

(defmethod type-check :recur-statement [statement scopes error-agent]
  (let [{:keys [pred args base]} statement
        pred-type (type-check pred scopes error-agent)
        base-type (type-check base scopes error-agent)
        method (:method scopes)
        return-type (:type method)
        required-args (:args method)]
    (assert-type pred-type :boolean
                 pred scopes error-agent)
    (assert-type base-type return-type
                 base scopes error-agent)
    (check-args args required-args statement scopes error-agent)
    ;; check that recur is from tail position
    (when-not (:tail? scopes)
      (send-off error-agent report-non-tail-return statement))))

(defn- binary-op-type-check [expression type scopes error-agent]
  (let [left  (:left expression)
        right (:right expression)
        left-type (type-check left scopes error-agent)
        right-type (type-check right scopes error-agent)]
    (assert-type left-type type
                 left scopes error-agent)
    (assert-type right-type type
                 right scopes error-agent)))

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
  (let [array (:array expression)
        index (:index expression)
        array-type (type-check array scopes error-agent)
        index-type (type-check index scopes error-agent)]
    (assert-type array-type :int<>
                 array scopes error-agent)
    (assert-type index-type :int
                 index scopes error-agent))
  :int)

(defmethod type-check :array-length-expression [expression scopes error-agent]
  (let [array (:array expression)
        array-type (type-check array scopes error-agent)]
    (assert-type array-type :int<>
                 array scopes error-agent))
  :int)

(defmethod type-check :method-call-expression [expression scopes error-agent]
  "Checks that the method calls an existing method with the appropriate
  arguments. Returns the return type of the method, or nil if not found."
  (let [{:keys [caller method args]} expression
        caller-type (if (= caller :this)
                      (-> scopes :class :name)
                      (type-check caller scopes error-agent))
        caller-class (-> scopes :class-table (get caller-type))]
    (if-let [method (locate-method caller-class method scopes)]
      ;; method found, check argument types
      ;; and return method's return type regardless of correct usage
      (do (check-args args (:args method) expression scopes error-agent)
          (:type method))
      ;; method not found
      (do (send-off error-agent report-missing-method
                    expression method)
          nil))))

(defmethod type-check :int-lit-expression [expression scopes error-agent]
  :int)

(defmethod type-check :boolean-lit-expression [expression scopes error-agent]
  :boolean)

(defmethod type-check :identifier-expression [expression scopes error-agent]
  "Returns the type of the variable which the identifier is bound to.
  If the variable does not exist, reports and error and returns nil."
  (or (:type (locate-var (:id expression) scopes))
      (do (send-off error-agent report-missing-symbol expression)
          nil)))

(defmethod type-check :array-instantiation-expression [expression scopes
                                                       error-agent]
  (let [size (:size expression)
        size-type (type-check size scopes error-agent)]
    (assert-type size-type :int
                 size scopes error-agent))
  :int<>)

(defmethod type-check :object-instantiation-expression [expression scopes
                                                        error-agent]
  (let [type (:type expression)]
    (assert-type-exists type (:class-table scopes) expression error-agent)
    type))

(defmethod type-check :not-expression [expression scopes error-agent]
  (let [operand (:operand expression)
        operand-type (type-check operand scopes error-agent)]
    (assert-type operand-type :boolean
                 operand scopes error-agent))
  :boolean)

(defmethod type-check :neg-expression [expression scopes error-agent]
  (let [operand (:operand expression)
        operand-type (type-check operand scopes error-agent)]
    (assert-type operand-type :int
                 operand scopes error-agent))
  :int)

(defn- locate-cyclic-class [parents visited error-agent]
  "Locates the first class in parents which is also in visited.
  Reports a cyclic inheritance error."
  (when (seq parents)
    (let [parent (first parents)
          parent-name (:name parent)]
      (if (visited parent-name)
        (do (send-off error-agent report-cyclic-inheritance parent)
            parent-name)
        (recur (next parents) (conj visited parent-name) error-agent)))))

(defn- remove-cycles [class-table error-agent]
  "Removes inheritance cycles from the class table by iterating through
  each class in the table and removing its parent reference if it is a
  child of itself."
  (-> (fn [class-table class-name]
        (let [class (class-table class-name)
              parents (parent-seq class class-table)]
          (if-let [cyclic-class-name
                   (locate-cyclic-class parents #{name} error-agent)]
            ;; remove parent reference from class which introduces
            ;; cyclic inheritance
            (update-in class-table [cyclic-class-name]
                       dissoc :parent)
            ;; no cyclic inheritance found, change nothing
            class-table)))
   (reduce class-table (keys class-table))))

(defn class-table [ast parser]
  (let [error-agent (agent [0 parser])
        ;; put main in class table
        class-table (info-map [(:main ast)] error-agent)
        ;; put other classes in class table
        class-table (-> (:classes ast)
                        ;; create class table
                        (info-map class-table error-agent)
                        ;; remove inheritance cycles
                        (remove-cycles error-agent))
        classes     (vals class-table)
        scopes      {:class-table class-table}]
    (doseq [class classes]
      (let [parents (parent-seq class class-table)
            scopes  (assoc scopes :parents parents)]
        (shadow-check   class parents error-agent)
        (override-check (:methods class) parents error-agent)
        (type-check     class scopes  error-agent)))

    (await error-agent)
    (shutdown-agents)

    [class-table (first @error-agent)]))
