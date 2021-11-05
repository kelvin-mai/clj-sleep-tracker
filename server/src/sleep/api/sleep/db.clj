(ns sleep.api.sleep.db
  (:require [sleep.utils.db :as db]))

(defn create-sleep
  [db data]
  (db/query-one! db
                 {:insert-into :sleep
                  :values [data]}))

(defn get-sleep-by-account-id
  [db account-id]
  (db/query! db
             {:select [:*]
              :from [:sleep]
              :where [:= :account-id account-id]}))

(defn get-sleep-by-id
  [db id]
  (db/query-one! db
                 {:select [:*]
                  :from [:sleep]
                  :where [:= :id id]}))

(defn update-sleep-by-id
  [db {:keys [id account-id]} data]
  (db/query-one! db
                 {:update :sleep
                  :set data
                  :where [:and
                          [:= :id id]
                          [:= :account-id account-id]]}))

(defn delete-sleep-by-id
  [db {:keys [id account-id]}]
  (db/query-one! db
                 {:delete-from :sleep
                  :where [:and
                          [:= :id id]
                          [:= :account-id account-id]]}))
