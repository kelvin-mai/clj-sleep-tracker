(ns sleep.routing.middleware
  (:require [buddy.auth :refer [authenticated?]]
            [sleep.routing.exception :as exception])
  (:import [java.util UUID]))

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
       (let [account-id (get-in request [:identity :account/id])]
       (if (authenticated? request)
         (handler (assoc request :account-id (UUID/fromString account-id)))
         (exception/response 401 "Unauthorized" request)))))})
