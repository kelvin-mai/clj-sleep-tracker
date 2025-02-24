(ns sleep.router.middleware
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as auth.middleware]
            [taoensso.telemere :as t]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.cors :as ring.cors]
            [sleep.router.exception :as exception]))

(comment ring.cors/wrap-cors)

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (assoc request :env env)))))})

(def wrap-logging
  {:name ::logging
   :wrap
   (fn [handler]
     (fn [request]
       (t/log! {:level :info
                :data (select-keys request [:request-method
                                            :headers
                                            :uri
                                            :parameters
                                            :identity
                                            :remote-addr])}
               [(:request-method request) (:uri request)])
       (handler request)))})

(def wrap-metadata
  {:name ::metadata
   :wrap
   (fn [handler]
     (fn [request]
       (let [response (handler request)
             meta (select-keys request
                               [:request-method
                                :headers
                                :uri
                                :parameters
                                :identity
                                :remote-addr])]
         (if (<= 200 (:status response) 299)
           (update response :body #(assoc % :meta meta))
           response))))})

(def wrap-authentication
  {:name ::authentication
   :wrap
   (fn [handler]
     (fn [request]
       (let [jwt-secret (get-in request [:env :jwt-secret])
             token-backend (backends/jws {:secret jwt-secret
                                          :token-name "Bearer"})
             wrapped-handler (auth.middleware/wrap-authentication handler token-backend)]
         (wrapped-handler request))))})

(def wrap-authorization
  {:name ::authorization
   :wrap
   (fn [handler]
     (fn [request]
       (if (authenticated? request)
         (handler request)
         (exception/throw-exception "Unauthorized" 401 :unauthorized))))})

(def wrap-cors
  {:name ::cors
   :wrap
   (fn [handler]
     (fn [request]
       (let [allowed-origin  "http://localhost:4000"
             allowed-methods "GET, PUT, PATCH, POST, DELETE, OPTIONS"
             allowed-headers "Authorization, Content-Type"
             max-age         600
             response        (handler request)]
         (-> response
             (assoc-in [:headers "Access-Control-Allow-Origin"] allowed-origin)
             (assoc-in [:headers "Access-Control-Allow-Methods"] allowed-methods)
             (assoc-in [:headers "Access-Control-Allow-Headers"] allowed-headers)
             (assoc-in [:headers "Access-Control-Max-Age"] max-age)))))})

(def global-middlewares
  [parameters/parameters-middleware
   muuntaja/format-middleware
   wrap-cors
   exception/exception-middleware
   coercion/coerce-response-middleware
   coercion/coerce-request-middleware
   wrap-metadata
   wrap-logging
   wrap-env])