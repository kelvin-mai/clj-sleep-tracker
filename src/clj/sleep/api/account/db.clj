(ns sleep.api.account.db
  (:require [buddy.hashers :as  hashers]
            [one-time.core :as ot]
            [sleep.utils.query :as q]))

(defn create-account!
  [db {:keys [email password]}]
  (let [data {:email      email
              :password   (hashers/derive password)
              :otp-secret (ot/generate-secret-key)}]
    (q/query-one! db
                  {:insert-into :account
                   :values      [data]})))

(defn get-account-by-email
  [db email]
  (q/query-one! db
                {:select [:*]
                 :from   [:account]
                 :where  [:= :email [:cast email :citext]]}))

(defn get-account-by-id
  [db id]
  (q/query-one! db
                {:select [:*]
                 :from   [:account]
                 :where  [:= :id id]}))

(defn update-account-password!
  [db id password]
  (q/query-one! db
                {:update :account
                 :set    {:password (hashers/derive password)}
                 :where  [:= :id id]}))

(defn store-refresh-token!
  [db data]
  (q/query-one! db
                {:insert-into :refresh-token
                 :values      [data]}))

(defn delete-refresh-token!
  [db jti]
  (q/query-one! db
                {:delete-from [:refresh-token]
                 :where       [:= :id jti]}))

(defn get-refresh-token-by-token-and-jti
  [db jti token]
  (q/query-one! db
                {:select [:*]
                 :from   [:refresh-token]
                 :where  [:and
                          [:= :id jti]
                          [:= :token token]]}))

(defn verify-account!
  [db email]
  (q/query-one! db
                {:update :account
                 :set    {:verified true}
                 :where  [:and
                          [:= :email email]
                          [:= :verified false]]}))