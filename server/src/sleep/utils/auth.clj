(ns sleep.utils.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.hashers :refer [check]]
            [buddy.sign.jwt :as jwt]))

(defn jwt-backend
  [secret]
  (backends/jws {:secret secret
                 :token-name "Bearer"}))

(defn account->response
  [account secret]
  (let [sanitized (dissoc account :account/password-hash)
        token (jwt/sign sanitized secret)]
    (assoc sanitized :account/token token)))

(defn password-match?
  [account password]
  (when (and account
             (check password (:account/password-hash account)))
    account))
