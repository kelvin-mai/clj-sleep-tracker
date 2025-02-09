(ns sleep.router.middleware
  (:require [buddy.auth :refer [authenticated?]]
            [tick.core :as t]
            [sleep.router.exception :as exception]))

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
           (if (t/< (t/instant) exp)
             (handler request)
             (exception/response 401 "Expired access token" request)))
         (exception/response 401 "Unauthorized" request))))})