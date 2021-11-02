(ns sleep.system.router
  (:require [integrant.core :as ig]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.malli :as malli]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [sleep.routing.middleware :as mw]
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
              :coercion malli/coercion
              :middleware [exception/exception-middleware
                           muuntaja/format-middleware
                           [wrap-authentication (auth/jwt-backend jwt-secret)]
                           coercion/coerce-exceptions-middleware
                           coercion/coerce-request-middleware
                           coercion/coerce-response-middleware
                           mw/wrap-env]}})
     (ring/routes
      (ring/redirect-trailing-slash-handler)))))
