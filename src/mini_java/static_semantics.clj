(ns mini-java.static-semantics
  "Performs static semantic checks on ASTs, and creates a class table.
  Static semantics includes type checking, name resolution, and minimal
  data flow analysis."
  (:require [mini-java.ast    :as    ast]
            [mini-java.errors :refer [print-error
                                      print-type-error
                                      print-symbol-error]]
            [mini-java.util   :as    util]))

(declare info type-check parent-seq)

(def ^:private context->type
  "Maps several context keywords to a string describing their type for use
  in error reporting."
  {:method-declaration "method",
   :class-declaration  "class",
   :var-declaration    "variable",
   :formal-parameter   "argument"})

(defn- type-from-context [x]
  (-> x ast/context context->type))

(def ^:private type-str-map
  {:int     "int",
   :boolean "boolean",
   :int<>   "int[]"})

(defn- type-str [type]
  "Returns the string representation of type"
  (get type-str-map type type))

(defn- arg-types-str [arg-types]
  "Returns the string representation of a list of argument types"
  (clojure.string/join "," (map type-str arg-types)))

(defn- report* [error-agent msg meta & {:keys [found  required
                                               symbol location]}]
  (let [{:keys [line column]} meta
        [error-count parser]  error-agent]
    (cond
      found  (print-type-error   parser msg line column found  required)
      symbol (print-symbol-error parser msg line column symbol location)
      :else  (print-error        parser msg line column))
    [(inc error-count) parser]))

(defn- report-duplicate [error-agent obj]
  "Reports a duplicate class/method/variable."
  (let [msg (str "duplicate " (type-from-context obj) ": " (:name obj))]
    (report* error-agent msg (meta obj))))

(defn- report-shadow [error-agent child var]
  "Reports a class shadowing one of its parents' fields."
  (let [msg (str "class "             (:name child)
                 " shadows variable " (:name var))]
    (report* error-agent msg (meta var))))

(defn- report-cyclic-inheritance [error-agent class]
  "Reports a cyclic inheritance."
  (let [msg (str "cyclic inheritance involving " (:name class))]
    (report* error-agent msg (meta class))))

(defn- report-bad-type [error-agent context found required]
  (let [msg "incompatible types"]
    (report* error-agent msg (meta context)
             :found    (type-str found)
             :required (type-str required))))

(defn- report-missing-symbol [error-agent context scopes]
  (let [symbol   (:id context)
        location (-> scopes :class :name)
        msg      "cannot find symbol"]
    (report* error-agent msg (meta context)
             :symbol   symbol
             :location location)))

(defn- report-missing-method [error-agent context method-name]
  (let [msg (str "cannot find method " method-name)]
    (report* error-agent msg (meta context))))

(defn- report-missing-type [error-agent context type]
  (let [msg (str "cannot find type " type)]
    (report* error-agent msg (meta context))))

(defn- report-use-before-init [error-agent context var-name]
  (let [msg (str "variable " var-name " might not have been initialized")]
    (report* error-agent msg (meta context))))

(defn- report-number-of-args [error-agent context n-required]
  (let [msg (str "wrong number of args given (" n-required " required)")]
    (report* error-agent msg (meta context))))

(defn- report-type-args [error-agent given-types required-types context]
  (let [msg "method cannot be applied to given types"]
    (report* error-agent msg (meta context)
             :found    (arg-types-str given-types)
             :required (arg-types-str required-types))))

(defn- report-overload [error-agent context child-type parent-type]
  (let [msg (str "method " (:name context) " overloads parent method")]
    (report* error-agent msg (meta context))))

(defn- report-return-type [error-agent context child-type parent-type]
  (let [msg (str "method " (:name context)
                 " overrides parent method with wrong type")]
    (report* error-agent msg (meta context)
             :found    (type-str child-type)
             :required (type-str parent-type))))

(defn- report-no-return [error-agent method]
  (let [msg (str "method " (:name method) " does not return")]
    (report* error-agent msg (meta method))))

(defn- report-non-tail-return [error-agent statement]
  (let [statement-type (if (= :return-statement
                              (ast/context statement))
                         "return"
                         "recur")
        msg (str statement-type " only allowed from tail position of method")]
    (report* error-agent msg (meta statement))))

(def ^:private primitive?
  #{:int :int<> :boolean})

(defn- subtype? [child parent class-table]
  "Returns whether or not child is a subtype of parent."
  (cond
    ;; anything is a subtype of itself
    (= child parent)
    true
    ;; primitives are _only_ subtypes of themselves
    (primitive? child)
    false
    ;; if everything above failed, must either be a true child of parent,
    ;; or just not a subtype
    :else
    (let [child-class (class-table child)]
      (when-let [parents (parent-seq child-class class-table)]
        (some (partial = parent)
              (map :name parents))))))

(defn- assert-type [found required context scopes error-agent]
  "Reports a type mismatch if the found type is not a subtype of the required
  type. If either type is nil, then the error occurred earlier, and would have
  been reported."
  (when (and found
             required
             (not (subtype? found required
                            (:class-table scopes))))
    (send-off error-agent report-bad-type context found required)))

(defn- assert-type-exists [type class-table context error-agent]
  "Reports a missing type if the given type does not exist."
  (when-not (or (primitive?  type)
                (class-table type))
    (send-off error-agent report-missing-type context type)))

(defn- info-map
  "Constructs a mapping of element names to the result of applying the info
  function to the elements in the given collection. If an element is already
  contained in the map, a duplicate error is reported. An initial map can be
  optionally provided."
  ([coll error-agent]
     (info-map coll {} error-agent))
  ([coll init error-agent]
     (-> (fn [r elem]
           (let [{:keys [name] :as info} (info elem error-agent)]
             (if (get r name)
               (do (send-off error-agent report-duplicate elem)
                   r)
               (assoc r name info))))
         (reduce init coll))))

(defmulti info
  "Takes a node in the AST, and extracts information from it. At the top level,
  info returns a class table."
  (fn [x & args] (ast/context x)))

(defmethod info :default [obj error-agent]
  "If none of the contexts is matched, it is a bug. Crash."
  (throw (ex-info "Unknown context."
                  {:type :unknown-context
                   :node obj})))

(defmethod info :var-declaration [var error-agent]
  "Variable declarations are always uninitialized at first."
  (assoc var :initialized? (atom false)))

(defmethod info :formal-parameter [var error-agent]
  "Formal parameters are argument variables, and therefore do not need to be
  initialized."
  (assoc var :initialized? (atom true)))

(defmethod info :method-declaration [method error-agent]
  "Extracts the information from a method declaration, consolidating its
  variables into an info-map, and reporting duplicates."
  (let [args (:args method)
        ;; create hash map of argument variables
        arg-vars (info-map args error-agent)]
    (-> {:name (:name method),
         :type (:type method),
         :args args,
         ;; combine local and argument variables into a single map
         :vars (info-map (:vars method) arg-vars error-agent)
         :body (:body method)}
      (with-meta (meta method)))))

(defmethod info :class-declaration [class error-agent]
  "Extracts the information from a class declaration, applying the info-map
  function to its methods and fields to both organize them and report
  duplicates."
  (-> {:name    (:name class),
       :parent  (:parent class),
       :vars    (info-map (:vars class) error-agent)
       :methods (info-map (:methods class) error-agent)}
    (with-meta (meta class))))

(defmethod info :main-class-declaration [class error-agent]
  "Rearranges the information in a main class declaration."
  (-> {:name    (:name class)
       :main?   true
       :vars    ()
       :methods {:main {:name "main"
                        :vars ()
                        :args ()
                        :body (:body class)}}}
    (with-meta (meta class))))

(defn parent-seq [class class-table]
  "Returns a recursive lazy seq of all parents of the given class."
  (lazy-seq
   (when-let [parent-name (:parent class)]
     (let [parent (class-table parent-name)]
       (cons parent (parent-seq parent class-table))))))

(defn locate-var [id scopes]
  "Locates the variable referenced by the id.
  First searches the variables local to the method, then the fields of the
  class, and finally recursively searches the fields of the parent class(es)."
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
  "Check that the number of given arguments match the number required for
  the method call."
  (let [n-given    (count given-args)
        n-required (count required-args)]
    (or
      ;; correct number of arguments given
      (= n-given n-required)
      ;; wrong number of arguments given
      (send-off error-agent report-number-of-args given-args n-required))))

(defn- check-arg-types [given-args required-args scopes error-agent]
  ""
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

(defmethod type-check :main-class-declaration [class scopes error-agent]
  (let [scopes (assoc scopes :class class)]
    (type-check (-> class :methods :main :body)
                scopes
                error-agent)))

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
            final-statement-type (ast/context final-statement)
            tail-rec? (or (= final-statement-type :return-statement)
                          (= final-statement-type :recur-statement))]
        (when-not tail-rec?
          (send-off error-agent report-no-return method))
        
        (type-check final-statement
                    (assoc scopes :tail-rec? tail-rec?) error-agent)))))

(defmethod type-check :nested-statement [statements scopes error-agent]
  (doseq [statement statements]
    (type-check statement scopes error-agent)))

(defn- get-uninitialized [vars]
  (set (filter (fn [[k v]] (not @(:initialized? v)))
               vars)))

(defn- deinitialize [uninitialized]
  (doseq [[name var] uninitialized]
    (reset! (:initialized? var) false)))

(defmethod type-check :if-else-statement [statement scopes error-agent]
  (let [pred (:pred statement)
        pred-type (type-check pred scopes error-agent)]
    (assert-type pred-type :boolean
                 pred scopes error-agent))
  (let [vars
        (-> scopes :method :vars)

        pre-uninitialized
        (get-uninitialized vars)

        _
        (type-check (:then statement) scopes error-agent)

        then-uninitialized
        (get-uninitialized vars)

        then-initialized
        (clojure.set/difference pre-uninitialized
                                then-uninitialized)

        _
        (deinitialize then-initialized)

        _
        (type-check (:else statement) scopes error-agent)

        else-uninitialized
        (get-uninitialized vars)

        either-uninitialized
        (util/two-way-set-difference then-uninitialized
                                     else-uninitialized)

        _
        (deinitialize either-uninitialized)]))

(defmethod type-check :while-statement [statement scopes error-agent]
  (let [pred (:pred statement)
        pred-type (type-check pred scopes error-agent)]
    (assert-type pred-type :boolean
                 pred scopes error-agent))
  (let [vars (-> scopes :method :vars)
        pre-uninitialized (get-uninitialized vars)]
    (type-check (:body statement) scopes error-agent)
    (let [post-uninitialized (get-uninitialized vars)
          either-uninitialized (util/two-way-set-difference pre-uninitialized
                                                            post-uninitialized)]
      (deinitialize either-uninitialized))))

(defmethod type-check :print-statement [statement scopes error-agent]
  "Check that print statement has an int as its argument."
  (let [arg (:arg statement)
        arg-type (type-check arg scopes error-agent)]
    (assert-type arg-type :int
                 arg scopes error-agent)))

(defmethod type-check :assign-statement [statement scopes error-agent]
  (let [{:keys [target source]} statement
        target-var (locate-var target scopes)
        source-type (type-check source scopes error-agent)]
    (if target-var
      (do (assert-type source-type (:type target-var)
                       source scopes error-agent)
          (reset! (:initialized? target-var) true))
      (send-off error-agent report-missing-symbol statement scopes))))

(defmethod type-check :array-assign-statement [statement scopes error-agent]
  (let [{:keys [target index source]} statement
        target-var  (locate-var target scopes)
        index-type  (type-check index scopes error-agent)
        source-type (type-check source scopes error-agent)]
    (assert-type (:type target-var) :int<>
                 index scopes error-agent)
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
    (when-not (:tail-rec? scopes)
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
    (when-not (:tail-rec? scopes)
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
  (let [var (locate-var (:id expression) scopes)]
    (if-not var
      (do (send-off error-agent report-missing-symbol expression scopes)
            nil)
      (let [init (:initialized? var)]
        ;; check for uninitialized locals
        (when-not @init
            (reset! init true)
            (send-off error-agent
                      report-use-before-init expression (:name var)))
        (:type var)))))

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
  (let [;; error agent keeps a count of all errors detected
        ;; errors are sent to it, and reported asynchronously
        error-agent (agent [0 parser])
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
