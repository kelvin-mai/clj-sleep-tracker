(ns sleep.router.middleware
  (:require [buddy.auth :refer [authenticated?]]
            [sleep.router.exception :as exception]
            [sleep.utils.time :refer [is-expired?]]))

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (assoc request :env env)))))})

(def wrap-authorization
  {:name ::authorization
   :wrap
   (fn [handler]
     (fn [{:keys [identity]
           :as   request}]
       (if (authenticated? request)
         (let [exp (:exp identity)]
           (if (is-expired? exp)
             (handler request)
             (exception/response 401 "Expired access token" request)))
         (exception/response 401 "Unauthorized" request))))})