(ns sleep.api.accounts.schema
  (:require [sleep.utils.schema :refer [non-blank-string?]]))

(def register-body
  [:map
   [:email non-blank-string?]
   [:password non-blank-string?]])

(def login-body register-body)

(def refresh-access-token-body
  [:map
   [:refresh-token non-blank-string?]])