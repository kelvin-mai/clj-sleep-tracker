(ns sleep.system.router
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.cors :refer [wrap-cors]]
            [sleep.router.coercion :as router.coercion]
            [sleep.router.exception :as exception]
            [sleep.router.middleware :as middlewares]
            [sleep.api.routes :refer [api-routes]]))

(defmethod ig/init-key :reitit/router
  [_ {:keys [db config]}]
  (let [jwt-secret (:jwt-secret config)]
    (println "initializing routes")
    (ring/ring-handler
     (ring/router
      api-routes
      {:data {:env        {:db         db
                           :jwt-secret jwt-secret}
              :muuntaja   m/instance
              :coercion   router.coercion/coercion
              :middleware [parameters/parameters-middleware
                           muuntaja/format-middleware
                           [wrap-cors
                            :access-control-allow-origin [#"http://localhost:3000"]
                            :access-control-allow-methods [:get :post :put :delete :options]
                            :access-control-allow-headers [:content-type :authorization]]
                           exception/exception-middleware
                           [wrap-authentication (backends/jws {:secret     jwt-secret
                                                               :token-name "Bearer"})]
                           coercion/coerce-response-middleware
                           coercion/coerce-request-middleware
                           middlewares/wrap-env]}})
     (ring/redirect-trailing-slash-handler))))