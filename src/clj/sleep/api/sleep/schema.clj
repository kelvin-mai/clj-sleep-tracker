(ns sleep.api.sleep.schema
  (:require [malli.util :as mu]
            [sleep.utils.schema :refer [date?
                                        time?]]))

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