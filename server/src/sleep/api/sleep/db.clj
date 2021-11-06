(ns sleep.api.sleep.db
  (:require [sleep.utils.db :as db]))

(defn create-sleep
  [db data]
  (db/query-one! db
                 {:insert-into :sleep
                  :values [data]}))

(defn get-sleep-by-account-id
  [db account-id query]
  (let [where-clause [:= :account-id account-id]
        where-clause (if (or (:start-date query)
                             (:end-date query))
                       [:and where-clause]
                       where-clause)
        where-clause (cond-> where-clause
                       (:start-date query)
                       (conj [:> :sleep-date (:start-date query)])
                       
                       (:end-date query)
                       (conj [:< :sleep-date (:end-date query)]))]
    (db/query! db
               {:select [:*]
                :from [:sleep]
                :where where-clause})))

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
