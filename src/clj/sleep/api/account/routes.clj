(ns sleep.api.account.routes
  (:require [sleep.api.account.schema :as account.schema]
            [sleep.api.account.handler :as account.handler]
            [sleep.router.response :refer [wrap-response-schema
                                           no-content-response]]
            [sleep.router.middleware :refer [wrap-authorization]]))

(def routes
  ["/account" {:tags #{"account"}
               :openapi {:security [{"JWT" []}]}
               :swagger {:security [{"JWT" []}]}}
   ["/" {:middleware [wrap-authorization]
         :get        {:responses {200 {:body (wrap-response-schema account.schema/check-identity-response)}}
                      :handler account.handler/check-identity}
         :delete     {:responses {200 {:body no-content-response}}
                      :handler account.handler/logout}}]
   ["/register" {:post {:parameters {:body account.schema/register-body}
                        :responses {201 {:body (wrap-response-schema account.schema/account-response)}}
                        :handler    account.handler/register}}]
   ["/login" {:post {:parameters {:body account.schema/login-body}
                     :responses {200 {:body (wrap-response-schema account.schema/account-response)}}
                     :handler    account.handler/login}}]
   ["/refresh" {:middleware [wrap-authorization]
                :post       {:parameters {:body account.schema/refresh-access-token-body}
                             :responses  {200 {:body (wrap-response-schema account.schema/refresh-access-token-response)}}
                             :handler    account.handler/refresh-access-token}}]
   ["/re-verify/:email" {:parameters {:path account.schema/re-verify-path-params}
                         :responses  {200 {:body no-content-response}}
                         :get        {:handler account.handler/new-verify-code}}]
   ["/verify/:id/:code" {:parameters {:path account.schema/verify-path-params}
                         :responses  {200 {:body (wrap-response-schema account.schema/account-response)}}
                         :get        {:handler account.handler/verify-account}}]])