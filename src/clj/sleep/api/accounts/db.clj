(ns sleep.api.accounts.db
  (:require [sleep.utils.db :as db]
            [buddy.hashers :refer [encrypt]]))

(defn create-account!
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

(defn get-account-by-id
  [db id]
  (db/query-one! db
                 {:select [:*]
                  :from   [:accounts]
                  :where  [:= :id id]}))

(defn update-account-password!
  [db id password]
  (db/query-one! db
                 {:update :accounts
                  :set    {:password password}
                  :where  [:= :id id]}))

(defn store-refresh-token!
  [db data]
  (db/query-one! db
                 {:insert-into :refresh-tokens
                  :values      [data]}))

(defn delete-refresh-token!
  [db jti]
  (db/query-one! db
                 {:delete-from [:refresh-tokens]
                  :where       [:= :id jti]}))

(defn get-refresh-token-by-token-and-jti
  [db jti token]
  (db/query-one! db
                 {:select [:*]
                  :from   [:refresh-tokens]
                  :where  [:and
                           [:= :id jti]
                           [:= :token token]]}))