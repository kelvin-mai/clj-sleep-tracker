(ns sleep.api.sleep.handler
  (:require [sleep.api.sleep.db :as sleep.db]
            [sleep.api.sleep.schema :as sleep.schema]
            [sleep.router.response :as response]
            [sleep.router.middleware :refer [wrap-authorization]]
            [sleep.router.exception :as exception]))

(defn get-sleeps
  [{:keys [parameters env identity]}]
  (let [db         (:db env)
        account-id (:sub identity)
        sleeps     (sleep.db/get-sleeps db account-id (:query parameters))]
    (response/ok sleeps)))

(defn create-sleep
  [{:keys [parameters env identity]}]
  (let [db    (:db env)
        data  (assoc (:body parameters)
                     :account-id (:sub identity))
        sleep (sleep.db/create-sleep! db data)]
    (if sleep
      (response/created sleep)
      (exception/throw-exception "Resource not found" 404 :not-found))))

(defn get-sleep
  [{:keys [parameters env identity]}]
  (let [db    (:db env)
        ident {:account-id (:sub identity)
               :date       (get-in parameters [:path :date])}
        sleep (sleep.db/get-sleep-by-date db ident)]
    (if sleep
      (response/ok sleep)
      (exception/throw-exception "Resource not found" 404 :not-found))))


(defn update-sleep
  [{:keys [parameters env identity]}]
  (let [db    (:db env)
        ident {:account-id (:sub identity)
               :date       (get-in parameters [:path :date])}
        data  (:body parameters)
        sleep (sleep.db/update-sleep! db ident data)]
    (if sleep
      (response/ok sleep)
      (exception/throw-exception "Resource not found" 404 :not-found))))

(defn delete-sleep
  [{:keys [parameters env identity]}]
  (let [db    (:db env)
        ident {:account-id (:sub identity)
               :date       (get-in parameters [:path :date])}
        sleep (sleep.db/delete-sleep! db ident)]
    (if sleep
      (response/ok sleep)
      (exception/throw-exception "Resource not found" 404 :not-found))))