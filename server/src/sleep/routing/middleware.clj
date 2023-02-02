(ns sleep.routing.middleware
  (:require [buddy.auth :refer [authenticated?]]
            [sleep.api.account.db :as account.db]
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
     (fn [{:keys [db] :as request}]
       (if (authenticated? request)
         (let [account-id (get-in request [:identity :account/id])
               account-id (UUID/fromString account-id)
               account (account.db/get-by-id db account-id)]
           (if account
             (handler (assoc request
                             :account-id account-id
                             :account account))
             (exception/response 401 "Malformed token" request)))
         (exception/response 401 "Unauthorized" request))))})