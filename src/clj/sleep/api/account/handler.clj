(ns sleep.api.account.handler
  (:require [next.jdbc :as jdbc]
            [buddy.sign.jwt :as jwt]
            [one-time.core :as ot]
            [sleep.api.account.db :as account.db]
            [sleep.api.account.utils :refer [sanitize-account
                                             password-match?
                                             generate-tokens!
                                             generate-access-token]]
            [sleep.router.response :as response]
            [sleep.utils.maps :refer [map->ns-map]]
            [sleep.router.exception :as exception]))

(defn register
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret
                mailer]} env
        data             (:body parameters)]
    (jdbc/with-transaction [tx db]
      (let [account (account.db/create-account! tx data)
            code    (ot/get-totp-token (:account/otp-secret account) {:time-step (* 15 60)})
            _       (when account
                      (.send! mailer
                              {:from    "noreply@sleep.com"
                               :to      (:account/email account)
                               :subject "Welcome to Sleep"
                               :body    (str "Welcome to Sleep. Please verify your email. Your code is "
                                             code
                                             "Please click on the link to verify your email."
                                             "link: http://localhost:8080/api/account/verify/"
                                             (:account/id account) "/" (:account/verification-code account))}))]
        (response/created (merge (sanitize-account account)
                                 (generate-tokens! tx
                                                   (:account/id account)
                                                   jwt-secret)))))))

(defn login
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret]}  env
        {:keys [email
                password]} (:body parameters)]
    (jdbc/with-transaction [tx db]
      (let [account (account.db/get-account-by-email tx email)
            account (password-match? account password)]
        (if account
          (response/ok (merge (sanitize-account account)
                              (generate-tokens! tx
                                                (:account/id account)
                                                jwt-secret)))
          (exception/throw-exception "Invalid credentials" 403 :invalid-credentials))))))

(defn check-identity
  [{:keys [identity env]}]
  (let [{:keys [db]} env
        id           (:sub identity)
        account      (account.db/get-account-by-id db id)]
    (if account
      (response/ok (merge (sanitize-account account)
                          (map->ns-map "claims" identity)))
      (exception/throw-exception "Invalid credentials" 403 :invalid-credentials))))

(defn logout
  [{:keys [identity env]}]
  (let [{:keys [db]} env
        jti          (:jti identity)
        _            (account.db/delete-refresh-token! db jti)]
    (response/ok)))

(defn refresh-access-token
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret]}    env
        {:keys [access-token
                refresh-token]} (:body parameters)
        {:keys [jti
                sub]}           (jwt/unsign access-token jwt-secret)
        refresh-token           (account.db/get-refresh-token-by-token-and-jti db jti refresh-token)]
    (if refresh-token
      (response/ok (assoc refresh-token
                          :refresh-token/access-token (generate-access-token jti sub jwt-secret)))
      (exception/throw-exception "Invalid refresh token" 403 :invalid-refresh-token))))

(defn new-verify-code
  [{:keys [parameters env]}]
  (let [{:keys [db
                mailer]} env
        email            (get-in parameters [:path :email])
        account          (account.db/get-account-by-email db email)
        code             (ot/get-totp-token (:account/otp-secret account) {:time-step (* 15 60)})
        _                (when account
                           (.send! mailer
                                   {:from    "noreply@sleep.com"
                                    :to      (:account/email account)
                                    :subject "We sent you a new verification code"
                                    :body    (str "Please verify your email. Your new code is "
                                                  code
                                                  "Please click on the link to verify your email."
                                                  "link: http://localhost:8080/api/account/verify/"
                                                  (:account/id account) "/" (:account/verification-code account))}))]
    (if account
      (response/ok)
      (exception/throw-exception "Invalid credentials" 403 :invalid-credentials))))

(defn verify-account
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret]} env
        email   (get-in parameters [:path :email])
        code    (get-in parameters [:body :code])
        account (account.db/get-account-by-email db email)
        valid?  (ot/is-valid-totp-token? code (:account/otp-secret account) {:time-step (* 15 60)})]
    (if valid?
      (jdbc/with-transaction [tx db]
        (let [account (account.db/verify-account! tx email)]
          (response/ok (merge (sanitize-account account)
                              (generate-tokens! tx
                                                (:account/id account)
                                                jwt-secret)))))
      (exception/throw-exception "Invalid verification code" 403 :invalid-verification-code))))