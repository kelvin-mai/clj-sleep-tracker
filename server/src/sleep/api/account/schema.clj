(ns sleep.api.account.schema
  (:require [sleep.utils.schema :refer [non-blank-string?]]))

(def login-body
  [:map 
   [:username non-blank-string?]
   [:password non-blank-string?]])

(def register-body
  [:map 
   [:username non-blank-string?]
   [:password non-blank-string?]])
