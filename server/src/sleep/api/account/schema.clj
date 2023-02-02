(ns sleep.api.account.schema
  (:require [sleep.utils.schema :refer [non-blank-string?]]))

(def auth-body
  [:map
   [:username non-blank-string?]
   [:password non-blank-string?]])