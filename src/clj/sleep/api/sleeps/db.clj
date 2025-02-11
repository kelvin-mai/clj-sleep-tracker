(ns sleep.api.sleeps.db
  (:require [sleep.utils.db :as db]))

(defn get-sleeps [db account-id]
  (db/query! db
             {:select [:*]
              :from   [:sleeps]
              :where  [:= :account-id account-id]}))

(defn create-sleep! [db data]
  (db/query-one! db
                 {:insert-into [:sleeps]
                  :values      [data]}))

(defn get-sleep-by-date [db {:keys [date account-id]}]
  (db/query-one! db
                 {:select [:*]
                  :from   [:sleeps]
                  :where  [:and
                           [:= :sleep-date date]
                           [:= :account-id account-id]]}))

(defn update-sleep! [db {:keys [date account-id]} data]
  (db/query-one! db
                 {:update [:sleeps]
                  :set    data
                  :where  [:and
                           [:= :sleep-date date]
                           [:= :account-id account-id]]}))

(defn delete-sleep! [db {:keys [date account-id]}]
  (db/query-one! db
                 {:delete-from [:sleeps]
                  :where       [:and
                                [:= :sleep-date date]
                                [:= :account-id account-id]]}))