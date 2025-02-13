(ns sleep.api.sleep.schema
  (:require [malli.util :as mu]
            [sleep.utils.schema :refer [date?
                                        time?
                                        timestamp?]]))

(def get-all-query
  (mu/optional-keys
   [:map
    [:start-date date?]
    [:end-date date?]]))

(def date-path-param
  [:map
   [:date date?]])

(def create-body
  [:map
   [:sleep-date date?]
   [:start-time time?]
   [:end-time time?]])

(def update-body
  (mu/optional-keys create-body))

(def sleep-response
  [:map
   [:sleep/account-id :string]
   [:sleep/sleep-date timestamp?]
   [:sleep/start-time timestamp?]
   [:sleep/end-time timestamp?]
   [:sleep/duration number?]
   [:sleep/created-at timestamp?]
   [:sleep/updated-at [:or timestamp? :nil]]])