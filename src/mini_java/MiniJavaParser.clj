(ns mini-java.MiniJavaParser
  (:gen-class
     :name      mini-java.MiniJavaParser
     :extends   mini_java.antlr.MiniJavaParser))


;; WORK IN PROGRESS ;;
(defn convert-tokens [this]
  "Converts the names of tokens from ugly names like \"ID\" into prettier
  names like \"identifier\"."
  (let [token-conversion
        {"ID"           "identifier"
         "INT"          "integer literal"
         "WS"           "whitespace"
         "COMMENT"      "block comment"
         "LINE_COMMENT" "line comment"}

        tokenNames (.tokenNames this)]
    (dotimes [i (alength tokenNames)]
      (when-let [conversion (token-conversion (aget tokenNames i))]
        (aset tokenNames i conversion)))))
