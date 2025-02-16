(ns sleep.api.routes
  (:require [reitit.swagger :as swagger]
            [reitit.openapi :as openapi]
            [sleep.router.response :as response]
            [sleep.api.account.routes :as account]
            [sleep.api.sleep.routes :as sleep]))

(def health-route
  ["/" {:name      ::health-check
        :tags      #{"health"}
        :summary   "Check if the server is up and running"
        :responses {200 {:body response/no-content-response}}
        :get       (fn [_] (response/ok))}])

(def swagger-route
  ["/swagger.json"
   {:get {:no-doc  true
          :swagger {:info                {:title       "Sleep API"
                                          :descriptoin "swagger2 docs for Sleep API"
                                          :version     "1.0.0"}
                    :securityDefinitions {"JWT" {:type :apiKey
                                                 :name :authorization
                                                 :in   :header}}}
          :handler (swagger/create-swagger-handler)}}])

(def openapi-route
  ["/openapi.json"
   {:get {:no-doc true
          :openapi {:info       {:title       "Sleep API"
                                 :description "openapi3 docs for Sleep API"
                                 :version     "1.0.0"}
                    :components {:securitySchemes {"JWT" {:type   :http
                                                          :scheme :bearer}}}}
          :handler (openapi/create-openapi-handler)}}])

(def api-routes
  ["/api"
   health-route
   account/routes
   sleep/routes])