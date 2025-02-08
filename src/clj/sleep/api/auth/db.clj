(ns sleep.api.auth.db
  (:require [sleep.utils.db :as db]
            [buddy.hashers :refer [encrypt]]))

(defn create-user
  [db {:keys [email password]}]
  (let [data {:email    email
              :password (encrypt password)}]
    (db/query-one! db
                   {:insert-into :users
                    :values      [data]})))

(defn get-user-by-email
  [db email]
  (db/query-one! db
                 {:select [:*]
                  :from   [:users]
                  :where  [:= :email email]}))