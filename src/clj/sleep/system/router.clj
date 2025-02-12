(ns sleep.system.router
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli]
            [sleep.router.coercion :refer [coercion]]
            [sleep.router.middleware :refer [create-global-middleware]]
            [sleep.router.exception :refer [default-handlers]]
            [sleep.api.routes :refer [api-routes]]))

(defmethod ig/init-key :reitit/router
  [_ {:keys [db config mailer]}]
  (let [jwt-secret (:jwt-secret config)]
    (println "initializing routes")
    (ring/ring-handler
     (ring/router
      api-routes
      {:data {:env        {:db         db
                           :jwt-secret jwt-secret
                           :mailer     mailer}
              :muuntaja   m/instance
              :coercion   coercion
              :middleware (create-global-middleware {:jwt-secret jwt-secret})}})
     (ring/routes
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler default-handlers)))))