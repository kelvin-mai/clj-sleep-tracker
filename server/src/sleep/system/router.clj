(ns sleep.system.router
  (:require [integrant.core :as ig]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.parameters :as parameters]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [sleep.routing.middleware :as mw]
            [sleep.routing.coercion :as routing.coercion]
            [sleep.routing.exception :as exception]
            [sleep.api.routes :refer [api-routes]]
            [sleep.utils.auth :as auth]))

(defmethod ig/init-key :reitit/routes
  [_ {:keys [db config]}]
  (let [jwt-secret (:jwt-secret config)]
    (println "initializing routes")
    (ring/ring-handler
     (ring/router
      api-routes
      {:data {:env {:db db
                    :jwt-secret jwt-secret}
              :muuntaja m/instance
              :coercion routing.coercion/coercion
              :middleware [parameters/parameters-middleware
                           muuntaja/format-middleware
                           exception/exception-middleware
                           [wrap-authentication (auth/jwt-backend jwt-secret)]
                           coercion/coerce-response-middleware
                           coercion/coerce-request-middleware
                           mw/wrap-env]}})
     (ring/routes
      (ring/redirect-trailing-slash-handler)))))
