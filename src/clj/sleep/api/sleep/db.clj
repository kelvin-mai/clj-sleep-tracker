(ns sleep.api.sleep.db
  (:require [sleep.utils.query :as q]))

(defn get-sleeps [db account-id query]
  (let [where-clause [:= :account-id account-id]
        where-clause (if (or (:start-date query)
                             (:end-date query))
                       [:and where-clause]
                       where-clause)
        where-clause (cond-> where-clause
                       (:start-date query)
                       (conj [>= :sleep-date (:start-date query)])
                       (:end-date query)
                       (conj [<= :sleep-date (:end-date query)]))]
    (q/query! db
              {:select [:*]
               :from   [:sleep]
               :where  where-clause})))

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