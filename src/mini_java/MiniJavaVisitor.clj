(ns mini-java.MiniJavaVisitor
  (:gen-class
     :name       mini-java.MiniJavaVisitor
     :implements [mini_java.antlr.MiniJavaVisitor]))

(defn -visit [this tree]
  (.visitMainClassDeclaration this (.getChild tree 0)))

(defn -visitMainClassDeclaration [this ctx]
  (.visitChildren this ctx))

(defn -visitMainClassDeclaration [this ctx]
  (println "heyyyy"))
