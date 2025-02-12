(ns sleep.router.middleware
  (:require [buddy.auth :refer [authenticated?]]
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
     (fn [request]
       (if (authenticated? request)
         (handler request)
         (throw (ex-info "Unauthorized" {:status 401
                                         :type   ::exception/unauthorized})))))})