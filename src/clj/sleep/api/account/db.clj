(ns sleep.api.account.db
  (:require [sleep.utils.db :as db]
            [buddy.hashers :refer [encrypt]]))

(defn create-account!
  [db {:keys [email password]}]
  (let [data {:email    email
              :password (encrypt password)}]
    (db/query-one! db
                   {:insert-into :account
                    :values      [data]})))

(defn get-account-by-email
  [db email]
  (db/query-one! db
                 {:select [:*]
                  :from   [:account]
                  :where  [:= :email email]}))

(defn get-account-by-id
  [db id]
  (db/query-one! db
                 {:select [:*]
                  :from   [:account]
                  :where  [:= :id id]}))

(defn update-account-password!
  [db id password]
  (db/query-one! db
                 {:update :account
                  :set    {:password password}
                  :where  [:= :id id]}))

(defn store-refresh-token!
  [db data]
  (db/query-one! db
                 {:insert-into :refresh-token
                  :values      [data]}))

(defn delete-refresh-token!
  [db jti]
  (db/query-one! db
                 {:delete-from [:refresh-token]
                  :where       [:= :id jti]}))

(defn get-refresh-token-by-token-and-jti
  [db jti token]
  (db/query-one! db
                 {:select [:*]
                  :from   [:refresh-token]
                  :where  [:and
                           [:= :id jti]
                           [:= :token token]]}))