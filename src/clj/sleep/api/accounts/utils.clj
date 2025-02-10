(ns sleep.api.accounts.utils
  (:require [buddy.hashers :refer [check]]
            [buddy.sign.jwt :as jwt]
            [tick.core :as t]
            [sleep.api.accounts.db :as accounts.db]
            [sleep.utils.time :refer [duration->instant]]))

(defn generate-initial-claims [sub]
  {:sub sub
   :iat (t/instant)})

(defn password-match?
  [user password]
  (when (and user
             (check password (:accounts/password user)))
    user))

(defn generate-access-token [jti sub secret]
  (jwt/sign (merge (generate-initial-claims sub)
                   {:jti jti
                    :exp (duration->instant 5 :minutes)})
            secret))

(defn generate-tokens! [db sub secret]
  (let [initial-claims       (generate-initial-claims sub)
        refresh-expires-at   (duration->instant 24 :hours)
        refresh-claims       (assoc initial-claims :exp
                                    refresh-expires-at)
        refresh-token        (jwt/sign refresh-claims secret)
        stored-refresh-token (accounts.db/store-refresh-token! db
                                                               {:token      refresh-token
                                                                :expires-at (t/date-time refresh-expires-at)})]
    (when stored-refresh-token
      {:refresh-token refresh-token
       :access-token  (generate-access-token (:refresh-tokens/id stored-refresh-token) sub secret)})))