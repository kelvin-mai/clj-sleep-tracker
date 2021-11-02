(ns sleep.routing.middleware
  (:require [buddy.auth :refer [authenticated?]]
            [sleep.routing.exception :as exception]))

(defn handle-wrap-env
  [request env]
  (-> request
      (assoc :db (:db env))
      (assoc :jwt-secret (:jwt-secret env))))

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (handle-wrap-env request env)))))})

(def wrap-authorization
  {:name ::authorization
   :wrap
   (fn [handler]
     (fn [request]
       (if (authenticated? request)
         (handler request)
         (exception/response 401 "Unauthorized" request))))})
