(ns sleep.api.routes
  (:require [sleep.api.accounts.handler :as accounts]
            [sleep.api.sleeps.handler :as sleeps]))

(def health-route
  ["/"
   {:name ::health-check
    :get (fn [_]
           {:status 200
            :body {:success true}})}])

(def api-routes
  [["/api"
    health-route
    accounts/routes
    sleeps/routes]])