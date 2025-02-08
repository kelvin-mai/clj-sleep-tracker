(ns sleep.api.auth.schema
  (:require [sleep.utils.schema :refer [non-blank-string?]]))

(def register-body
  [:map
   [:email non-blank-string?]
   [:password non-blank-string?]])