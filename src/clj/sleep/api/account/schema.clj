(ns sleep.api.account.schema
  (:require [malli.util :as mu]
            [sleep.utils.schema :refer [non-blank-string?
                                        email?
                                        timestamp?]]))

(def register-body
  [:and
   [:map
    [:email email?]
    [:password non-blank-string?]
    [:confirm-password non-blank-string?]]
   [:fn {:error/message "passwords must match"
         :error/path    [:confirm-pasword]}
    (fn [{:keys [password confirm-password]}]
      (= password confirm-password))]])

(def login-body
  [:map
   [:email email?]
   [:password non-blank-string?]])

(def refresh-access-token-body
  [:map
   [:refresh-token non-blank-string?]])

(def verify-path-params
  [:map
   [:id non-blank-string?]
   [:code :uuid]])

(def re-verify-path-params
  [:map
   [:email non-blank-string?]])

(def sanitized-account
  [:map
   [:account/id non-blank-string?]
   [:account/email email?]
   [:account/created-at timestamp?]
   [:account/updated-at [:or timestamp? :nil]]
   [:account/verified :boolean]])

(def account-response
  (mu/merge
   sanitized-account
   [:map
    [:token/refresh-token non-blank-string?]
    [:token/access-token non-blank-string?]]))

(def check-identity-response
  (mu/merge
   sanitized-account
   [:map
    [:claims/sub non-blank-string?]
    [:claims/exp :int]
    [:claims/iat :int]
    [:claims/jti [:or non-blank-string? :nil]]]))

(def refresh-access-token-response
  [:map
   [:refresh-token/id non-blank-string?]
   [:refresh-token/token non-blank-string?]
   [:refresh-token/expiration timestamp?]
   [:refresh-token/access-token non-blank-string?]])