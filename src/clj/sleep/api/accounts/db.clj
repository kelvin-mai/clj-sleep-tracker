(ns sleep.api.accounts.db
  (:require [sleep.utils.db :as db]
            [buddy.hashers :refer [encrypt]]))

(defn create-account
  [db {:keys [email password]}]
  (let [data {:email    email
              :password (encrypt password)}]
    (db/query-one! db
                   {:insert-into :accounts
                    :values      [data]})))

(defn get-account-by-email
  [db email]
  (db/query-one! db
                 {:select [:*]
                  :from   [:accounts]
                  :where  [:= :email email]}))

(defn update-account-password
  [db id password]
  (db/query-one! db
                 {:update :accounts
                  :set    {:password password}
                  :where  [:= :id id]}))