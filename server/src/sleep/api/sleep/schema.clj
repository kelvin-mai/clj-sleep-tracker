(ns sleep.api.sleep.schema
  (:require [sleep.utils.schema :refer [time? date?]]))

(def get-all-query
  [:map
   [:start-date {:optional true} date?]
   [:end-date {:optional true} date?]])

(def create-body
  [:map
   [:sleep-date date?]
   [:start-time time?]
   [:end-time time?]])

(def update-body
  [:map
   [:sleep-date {:optional true} date?]
   [:start-time {:optional true} time?]
   [:end-time {:optional true} time?]])
