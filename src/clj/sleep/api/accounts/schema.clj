(ns sleep.api.accounts.schema
  (:require [sleep.utils.schema :refer [non-blank-string?]]))

(def register-body
  [:and
   [:map
    [:email non-blank-string?]
    [:password non-blank-string?]
    [:confirm-password non-blank-string?]]
   [:fn {:error/message "passwords must match"
         :error/path    [:confirm-pasword]}
    (fn [{:keys [password confirm-password]}]
      (= password confirm-password))]])

(def login-body
  [:map
   [:email non-blank-string?]
   [:password non-blank-string?]])

(def refresh-access-token-body
  [:map
   [:refresh-token non-blank-string?]])