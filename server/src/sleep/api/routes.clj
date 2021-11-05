(ns sleep.api.routes
  (:require [sleep.api.account.handler :as account]
            [sleep.api.sleep.handler :as sleep]))

(def health-route
  ["/health-check"
   {:name ::health-check
    :get (fn [_]
           {:status 200
            :body {:ping "pong"}})}])

(def api-routes
  [["/api"
    health-route
    account/routes
    sleep/routes]])
