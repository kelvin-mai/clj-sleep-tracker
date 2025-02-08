(ns sleep.system.router
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.cors :refer [wrap-cors]]))

(defmethod ig/init-key :reitit/router
  [_ {:keys [db config]}]
  (let [jwt-secret (:jwt-secret config)]
    (println "initializing routes")
    (ring/ring-handler
     (ring/router
      []
      {:data {:env        {:db         db
                           :jwt-secret jwt-secret}
              :muuntaja   m/instance
              :coercion   reitit.coercion.malli/coercion
              :middleware [parameters/parameters-middleware
                           muuntaja/format-middleware
                           [wrap-cors
                            :access-control-allow-origin [#"http://localhost:3000"]
                            :access-control-allow-methods [:get :post :put :delete :options]
                            :access-control-allow-headers [:content-type :authorization]]
                           coercion/coerce-response-middleware
                           coercion/coerce-request-middleware]}}))))