(ns sleep.api.sleep.db
  (:require [sleep.utils.query :as q]))

(defn get-sleeps [db account-id]
  (q/query! db
            {:select [:*]
             :from   [:sleep]
             :where  [:= :account-id account-id]}))

(defn create-sleep! [db data]
  (q/query-one! db
                {:insert-into [:sleep]
                 :values      [data]}))

(defn get-sleep-by-date [db {:keys [date account-id]}]
  (q/query-one! db
                {:select [:*]
                 :from   [:sleep]
                 :where  [:and
                          [:= :sleep-date date]
                          [:= :account-id account-id]]}))

(defn update-sleep! [db {:keys [date account-id]} data]
  (q/query-one! db
                {:update [:sleep]
                 :set    data
                 :where  [:and
                          [:= :sleep-date date]
                          [:= :account-id account-id]]}))

(defn delete-sleep! [db {:keys [date account-id]}]
  (q/query-one! db
                {:delete-from [:sleep]
                 :where       [:and
                               [:= :sleep-date date]
                               [:= :account-id account-id]]}))