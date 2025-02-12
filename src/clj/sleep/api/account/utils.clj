(ns sleep.api.account.utils
  (:require [buddy.hashers :refer [check]]
            [buddy.sign.jwt :as jwt]
            [tick.core :as t]
            [sleep.api.account.db :as account.db]
            [sleep.utils.time :refer [duration->instant]]
            [sleep.utils.maps :refer [map->ns-map]]))

(defn sanitize-account [account]
  (dissoc account
          :account/password
          :account/verification-code
          :account/verification-code-expiration))

(defn generate-initial-claims [sub]
  {:sub sub
   :iat (t/instant)})

(defn password-match?
  [user password]
  (when (and user
             (check password (:account/password user)))
    user))

(defn generate-access-token [jti sub secret]
  (jwt/sign (merge (generate-initial-claims sub)
                   {:jti jti
                    :exp (duration->instant 15 :minutes)})
            secret))

(defn generate-tokens! [db sub secret]
  (let [initial-claims       (generate-initial-claims sub)
        refresh-expiration   (duration->instant 24 :hours)
        refresh-claims       (assoc initial-claims :exp
                                    refresh-expiration)
        refresh-token        (jwt/sign refresh-claims secret)
        stored-refresh-token (account.db/store-refresh-token! db
                                                              {:token      refresh-token
                                                               :expiration (t/date-time refresh-expiration)})]
    (when stored-refresh-token
      (map->ns-map "tokens"
                   {:refresh-token refresh-token
                    :access-token  (generate-access-token (:refresh-token/id stored-refresh-token) sub secret)}))))