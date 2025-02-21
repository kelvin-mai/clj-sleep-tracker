(ns sleep.router.middleware
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [taoensso.telemere :as t]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.cors :refer [wrap-cors]]
            [sleep.router.exception :as exception]))

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

(def wrap-authorization
  {:name ::authorization
   :wrap
   (fn [handler]
     (fn [request]
       (if (authenticated? request)
         (handler request)
         (exception/throw-exception "Unauthorized" 401 :unauthorized))))})

(defn create-global-middleware [{:keys [jwt-secret]}]
  [parameters/parameters-middleware
   muuntaja/format-middleware
   [wrap-cors
    :access-control-allow-origin  [#"http://localhost:4000"]
    :access-control-allow-methods [:get :post :put :delete :options]
    :access-control-allow-headers [:content-type :authorization]]
   exception/exception-middleware
   [wrap-authentication (backends/jws {:secret     jwt-secret
                                       :token-name "Bearer"})]
   coercion/coerce-response-middleware
   coercion/coerce-request-middleware
   wrap-metadata
   wrap-logging
   wrap-env])