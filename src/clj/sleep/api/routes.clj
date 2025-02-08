(ns sleep.api.routes
  (:require [sleep.api.auth.handler :as auth]))

(def health-route
  ["/"
   {:name ::health-check
    :get (fn [_]
           {:status 200
            :body {:success true}})}])

(def api-routes
  [["/api"
    health-route
    auth/routes]])