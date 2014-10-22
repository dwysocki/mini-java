(ns mini-java.MiniJavaVisitor
  (:require [mini-java.tree-values :as t])
  (:gen-class
     :name       mini-java.MiniJavaVisitor
     :implements [mini_java.antlr.MiniJavaVisitor]))

(defn -visit [this tree]
  (let [num-children (.getChildCount tree)
        children (map #(.getChild tree %) (range num-children))
        main-declaration (first children)
        class-declarations (-> children rest butlast)]
    (let [main-class (.visitMainClassDeclaration this main-declaration)
          classes    (doall (map #(.visitClassDeclaration this %)
                                 class-declarations))]
      (println "<Main>")
      (println main-class)
      (println "</Main>")
      (println "<Classes>")
      (doseq [c classes] (println c))
      (println "</Classes>"))))

(defn -visitMainClassDeclaration [this ctx]
  (let [[name _ body] (t/class-attrs ctx)]
    {:name name
     :body body}))

(defn -visitClassDeclaration [this ctx]
  (let [[name parent body] (t/class-attrs ctx)]
    {:name name
     :body body
     :parent parent}))
