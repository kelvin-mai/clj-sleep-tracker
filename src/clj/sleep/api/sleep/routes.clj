(ns sleep.api.sleep.routes
  (:require [sleep.api.sleep.schema :as sleep.schema]
            [sleep.api.sleep.handler :as sleep.handler]
            [sleep.router.response :as response]
            [sleep.router.middleware :refer [wrap-authorization]]))

(def routes
  ["/sleep" {:tags       #{"sleep"}
             :middleware [wrap-authorization]
             :openapi    {:security [{"JWT" []}]}
             :swagger    {:security [{"JWT" []}]}}
   ["" {:get  {:parameters {:query sleep.schema/get-all-query}
               :responses  {201 {:body (response/wrap-response-schema [:vector sleep.schema/sleep-response])}}
               :handler    sleep.handler/get-sleeps}
        :post {:parameters {:body sleep.schema/create-body}
               :responses  {201 {:body (response/wrap-response-schema sleep.schema/sleep-response)}}
               :handler    sleep.handler/create-sleep}}]
   ["/:date" {:parameters {:path sleep.schema/date-path-param}
              :get        {:responses {201 {:body (response/wrap-response-schema sleep.schema/sleep-response)}}
                           :handler   sleep.handler/get-sleep}
              :put        {:parameters {:body sleep.schema/update-body}
                           :responses  {201 {:body (response/wrap-response-schema sleep.schema/sleep-response)}}
                           :handler    sleep.handler/update-sleep}
              :delete     {:responses {201 {:body (response/wrap-response-schema sleep.schema/sleep-response)}}
                           :handler   sleep.handler/delete-sleep}}]])