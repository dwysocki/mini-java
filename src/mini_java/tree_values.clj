(ns mini-java.tree-values)

(defn terminal-node-text [node]
  (-> node .-symbol .getText))

(defn type-name [ctx]
  (terminal-node-text (.getChild ctx 0)))

(defn class-attrs [ctx]
  "Returns the name, parent class, and body of the given class context in
  a length 3 vector.

  If class does not have a parent, parent is nil."
  (let [child? (= 5 (.getChildCount ctx))
        name   (terminal-node-text (.getChild ctx 1))
        parent (when child? (type-name (.getChild ctx 3)))
        idx    (if child? 4 2)
        body   (.getChild ctx idx)]
    [name parent body]))
