(ns mini-java.ErrorHandler
  "Extends ANTLR's DefaultErrorStrategy to implement custom error messages."
  (:require [mini-java.errors :refer [print-error]]
            [mini-java.util   :as    util])
  (:gen-class
     :name mini-java.ErrorHandler
     :extends org.antlr.v4.runtime.DefaultErrorStrategy
     :exposes {errorRecoveryMode {:get inRecoveryMode}}
     :exposes-methods {getExpectedTokens    parentGetExpectedTokens
                       getTokenErrorDisplay parentGetTokenErrorDisplay
                       beginErrorCondition  parentBeginErrorCondition}))

(defn- expecting-str
  "Returns a string representation of the expected tokens."
  [parser exception]
  (let [tokens     (.getExpectedTokens exception)
        count      (.size tokens)
        tokens-str (.toString tokens (.getTokenNames parser))
        expecting  (cond
                    (zero? count) nil
                    (= 1   count) "expecting "
                    :else         "expecting one of ")]
    (when expecting
      (str expecting tokens-str))))

(defn -reportInputMismatch
  "Reports an input mismatch error."
  [this parser exception]
  (let [token     (.getOffendingToken exception)
        token-str (str "'" (.getText token) "'")
        expecting (expecting-str parser exception)
        msg (if expecting
              (str "found " token-str ", " expecting)
              (str "unexpected " token-str))]
    (.notifyErrorListeners parser msg)))

(defn -reportMissingToken
  "Reports a missing token."
  [this parser]
  ;; in error recovery mode, this method does nothing
  (when-not (.inErrorRecoveryMode this parser)
    (.parentBeginErrorCondition this parser)
    (let [token (.getCurrentToken parser)
          expecting (.parentGetExpectedTokens this parser)
          msg (str "missing "
                   (.toString expecting
                              (.getTokenNames parser)))]
      (.notifyErrorListeners parser msg))))

(defn -reportUnwantedToken
  "Reports an unwanted token."
  [this parser]
  ;; in error recovery mode, this method does nothing
  (when-not (.inErrorRecoveryMode this parser)
    (.parentBeginErrorCondition this parser)
    (let [token (.getCurrentToken parser)
          msg (str "extraneous '"
                   (.getText token)
                   "' inserted")]
      (.notifyErrorListeners parser msg))))

(defn -reportNoViableAlternative
  "Reports an unexpected token with no viable alternative."
  [this parser exception]
  (let [token (.getCurrentToken parser)
        context (.getContext parser)
        msg (str "unexpected " (.getText token))]
    (.notifyErrorListeners parser msg)))
