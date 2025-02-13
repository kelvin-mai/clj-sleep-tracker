(ns sleep.system.router
  (:require [taoensso.telemere :as t]
            [integrant.core :as ig]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.malli]
            [sleep.router.coercion :refer [coercion]]
            [sleep.router.middleware :refer [create-global-middleware]]
            [sleep.router.exception :refer [default-handlers]]
            [sleep.api.routes :refer [swagger-route
                                      openapi-route
                                      api-routes]]))

(defmethod ig/init-key :reitit/router
  [_ {:keys [db config mailer]}]
  (let [jwt-secret (:jwt-secret config)]
    (t/log! :info "initializing routes")
    (ring/ring-handler
     (ring/router
      [swagger-route
       openapi-route
       api-routes]
      {:data {:env        {:db         db
                           :jwt-secret jwt-secret
                           :mailer     mailer}
              :muuntaja   m/instance
              :coercion   coercion
              :middleware (create-global-middleware {:jwt-secret jwt-secret})}})
     (ring/routes
      (swagger-ui/create-swagger-ui-handler
       {:path "/"
        :config {:validatorUrl nil
                 :urls [{:name "swagger" :url "swagger.json"}
                        {:name "openapi" :url "openapi.json"}]
                 :urls.primaryName "openapi"
                 :operationSorter "alpha"}})
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler default-handlers)))))