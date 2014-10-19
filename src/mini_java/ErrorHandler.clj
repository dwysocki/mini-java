(ns mini-java.ErrorHandler
  (:require [mini-java.errors :refer [print-error]]
            [mini-java.util   :as    util])
  (:gen-class
     :name mini-java.ErrorHandler
     :extends org.antlr.v4.runtime.DefaultErrorStrategy
     :exposes {errorRecoveryMode {:get recoveryMode}}
     :exposes-methods {getExpectedTokens    parentGetExpectedTokens
                       getTokenErrorDisplay parentGetTokenErrorDisplay
                       beginErrorCondition  parentBeginErrorCondition}))

(defn -reportInputMismatch [this parser exception]
  (let [token     (->> exception
                       .getOffendingToken
                       (.parentGetTokenErrorDisplay this))
        expecting (-> exception
                      .getExpectedTokens
                      (.toString (.getTokenNames parser)))
        [line column] (util/token-line-and-column token)
        msg  (str "found " token ", expecting one of " expecting)]
    (print-error parser msg line column)))

(defn -reportMissingToken [this parser]
  (when-not (.recoveryMode this) (.parentBeginErrorCondition this parser))
  (let [token (.getCurrentToken parser)
        expecting (.parentGetExpectedTokens this parser)
        [line column] (util/token-line-and-column token)
        msg (str "missing "
                 (.toString expecting
                            (.getTokenNames parser)))]
    (print-error parser msg line column)))

(defn -reportUnwantedToken [this parser]
  (when-not (.recoveryMode this) (.parentBeginErrorCondition this parser))
  (let [token (.getCurrentToken parser)
        [line column] (util/token-line-and-column token)
        msg (str "extraneous " (.getText token) " inserted")]
    (print-error parser msg line column)))

(defn -reportNoViableAlternative [this parser exception]
  (let [msg "syntax error"
        token (.getOffendingToken exception)
        [line column] (util/token-line-and-column token)]
    (print-error parser msg line column)))

