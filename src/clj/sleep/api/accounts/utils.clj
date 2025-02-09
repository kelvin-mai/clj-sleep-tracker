(ns sleep.api.accounts.utils
  (:require [buddy.hashers :refer [check]]
            [buddy.sign.jwt :as jwt]
            [tick.core :as t]))

(defn sanitize-user [user]
  (dissoc user :accounts/password))

(defn password-match?
  [user password]
  (when (and user
             (check password (:accounts/password user)))
    user))

(defn generate-token [ttl sub secret]
  (let [claims {:sub sub
                :iat (t/format (t/instant))
                :exp (t/format (t/>> (t/instant)
                                     ttl))}]
    (jwt/sign claims secret)))

(def generate-access-token (partial generate-token (t/new-duration 15 :minutes)))