(ns sleep.api.account.db
  (:require [sleep.utils.db :as db]
            [buddy.hashers :refer [encrypt]]))

(defn create-account
  [db {:keys [username password]}]
  (let [hashed (encrypt password)
        data {:username username
              :password-hash hashed}]
    (db/query-one! db
                   {:insert-into :account
                    :values [data]})))

(defn get-by-username
  [db username]
  (db/query-one! db
                 {:select [:*]
                  :from [:account]
                  :where [:= :username username]}))
