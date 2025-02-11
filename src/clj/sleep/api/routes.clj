(ns sleep.api.routes
  (:require [sleep.api.account.handler :as account]
            [sleep.api.sleep.handler :as sleep]))

(def health-route
  ["/"
   {:name ::health-check
    :get (fn [_]
           {:status 200
            :body {:success true}})}])

(def api-routes
  [["/api"
    health-route
    account/routes
    sleep/routes]])