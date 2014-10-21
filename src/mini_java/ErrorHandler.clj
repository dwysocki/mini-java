(ns mini-java.ErrorHandler
  (:require [mini-java.errors :refer [print-error]]
            [mini-java.util   :as    util])
  (:gen-class
     :name mini-java.ErrorHandler
     :extends org.antlr.v4.runtime.DefaultErrorStrategy
     :exposes {errorRecoveryMode {:get inRecoveryMode}}
     :exposes-methods {getExpectedTokens    parentGetExpectedTokens
                       getTokenErrorDisplay parentGetTokenErrorDisplay
                       beginErrorCondition  parentBeginErrorCondition}))

(defn- expecting-str [parser exception]
  (let [tokens     (.getExpectedTokens exception)
        count      (.size tokens)
        tokens-str (.toString tokens (.getTokenNames parser))
        expecting  (cond
                    (zero? count) nil
                    (= 1   count) "expecting "
                    :default      "expecting one of ")]
    (when expecting
      (str expecting tokens-str))))

(defn- input-mismatch-msg [parser exception token-str]
  (case token-str
    nil))

(defn -reportInputMismatch [this parser exception]
  (let [token     (.getOffendingToken exception)
        token-str (str "'" (.getText token) "'")
        expecting (expecting-str parser exception)
        msg (if expecting
              (str "found " token-str ", " expecting)
              (str "unexpected " token-str))]
    (.notifyErrorListeners parser msg)))

(defn -reportMissingToken [this parser]
  (when-not (.inErrorRecoveryMode this parser)
    (.parentBeginErrorCondition this parser)
    (let [token (.getCurrentToken parser)
          expecting (.parentGetExpectedTokens this parser)
          msg (str "missing "
                   (.toString expecting
                              (.getTokenNames parser)))]
      (.notifyErrorListeners parser msg))))

(defn -reportUnwantedToken [this parser]
  (when-not (.inErrorRecoveryMode this parser)
    (.parentBeginErrorCondition this parser)
    (let [token (.getCurrentToken parser)
          msg (str "extraneous '" (.getText token) "' inserted")]
      (.notifyErrorListeners parser msg))))

(defn- alternative-msg [token context]
  (let [token-str (.getText token)]
    (case token-str
      "return"
      "return outside end of method"

      "recur"
      "recur outside end of method"

      (str "unexpected " token-str))))

(defn -reportNoViableAlternative [this parser exception]
  (let [token (.getCurrentToken parser)
        context (.getContext parser)
        msg (alternative-msg token context)]
    (.notifyErrorListeners parser msg)))
