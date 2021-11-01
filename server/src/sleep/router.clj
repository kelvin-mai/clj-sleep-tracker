(ns sleep.router
  (:require [integrant.core :as ig]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]))

(def health-route
  ["/health-check"
   {:name ::health-check
    :get (fn [_]
           {:status 200
            :body {:ping "pong"}})}])

(defmethod ig/init-key :reitit/routes
  [_ config]
  (println "initializing routes")
  (ring/ring-handler
    (ring/router
      [["/api"
        health-route]]
      {:data {:muuntaja m/instance
              :middleware [exception/exception-middleware
                           muuntaja/format-middleware]}})
    (ring/routes
      (ring/redirect-trailing-slash-handler))))
