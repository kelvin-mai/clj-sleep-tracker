(ns sleep.api.account.handler
  (:require [sleep.api.account.db :as account.db]
            [sleep.api.account.schema :as account.schema]
            [sleep.api.account.utils :refer [password-match?
                                             generate-tokens!
                                             generate-access-token]]
            [sleep.router.middleware :refer [wrap-authorization]]
            [sleep.router.response :as response]
            [sleep.utils.maps :refer [map->ns-map]]
            [sleep.router.exception :as exception]))

(defn register
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret
                mailer]} env
        data                    (:body parameters)
        account                 (account.db/create-account! db data)
        _                       (when account
                                  (.send! mailer
                                          {:from    "noreply@sleep.com"
                                           :to      (:account/email account)
                                           :subject "Welcome to Sleep"
                                           :body    (str "Welcome to Sleep. Please verify your email. Please click on the link to verify your email."
                                                         "link: http://localhost:8080/api/account/verify/"
                                                         (:account/id account) "/" (:account/verification-code account))}))]
    (response/created (-> account
                          (dissoc :account/password)
                          (merge (generate-tokens! db
                                                   (:account/id account)
                                                   jwt-secret))))))

(defn login
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret]}  env
        {:keys [email
                password]} (:body parameters)
        account                  (account.db/get-account-by-email db email)
        account                  (password-match? account password)]
    (if account
      (response/ok (-> account
                       (dissoc :account/password)
                       (merge (generate-tokens! db
                                                (:account/id account)
                                                jwt-secret))))
      (exception/throw-exception "Invalid credentials" 403 :invalid-credentials))))

(defn check-identity
  [{:keys [identity env]}]
  (let [{:keys [db]} env
        id           (:sub identity)
        account      (account.db/get-account-by-id db id)]
    (if account
      (response/ok (-> account
                       (dissoc :account/password)
                       (merge (map->ns-map "claims" identity))))
      (exception/throw-exception "Invalid credentials" 403 :invalid-credentials))))

(defn logout
  [{:keys [identity env]}]
  (let [{:keys [db]} env
        jti          (:jti identity)
        _            (account.db/delete-refresh-token! db jti)]
    (response/ok {})))

(defn refresh-access-token
  [{:keys [identity parameters env]}]
  (let [{:keys [db
                jwt-secret]} env
        {:keys [jti
                sub]}       identity
        token                   (get-in parameters [:body :refresh-token])
        refresh-token           (account.db/get-refresh-token-by-token-and-jti db jti token)]
    (if refresh-token
      (response/ok (assoc refresh-token
                          :refresh-token/access-token (generate-access-token jti sub jwt-secret)))
      (exception/throw-exception "Invalid refresh token" 403 :invalid-refresh-token))))

(defn verify-account
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret]} env
        {:keys [id
                code]}       (:path parameters)
        account              (account.db/verify-account! db id code)]
    (if account
      (response/ok (-> account
                       (dissoc :account/password)
                       (merge (generate-tokens! db
                                                (:account/id account)
                                                jwt-secret))))
      (exception/throw-exception "Invalid verification code" 403 :invalid-verification-code))))

(defn new-verify-code
  [{:keys [parameters env]}]
  (let [{:keys [db
                mailer]} env
        {:keys [email]}       (:path parameters)
        account               (account.db/regenerate-verification-code! db email)
        _                     (when account
                                (.send! mailer
                                        {:from    "noreply@sleep.com"
                                         :to      (:account/email account)
                                         :subject "We sent you a new verification code"
                                         :body    (str "We sent you a new verification code. Please verify your email. Please click on the link to verify your email."
                                                       "link: http://localhost:8080/api/account/verify/"
                                                       (:account/id account) "/" (:account/verification-code account))}))]
    (if account
      (response/ok (-> account
                       (dissoc :account/password)))
      (exception/throw-exception "Invalid credentials" 403 :invalid-credentials))))

(def routes
  ["/account"
   ["/" {:middleware [wrap-authorization]
         :get        check-identity
         :delete     logout}]
   ["/register" {:post {:parameters {:body account.schema/register-body}
                        :handler    register}}]
   ["/login" {:post {:parameters {:body account.schema/login-body}
                     :handler    login}}]
   ["/refresh" {:middleware [wrap-authorization]
                :post       {:parameters {:body account.schema/refresh-access-token-body}
                             :handler    refresh-access-token}}]
   ["/reverify/:email" {:parameters {:path account.schema/reverify-path-params}
                        :get        new-verify-code}]
   ["/verify/:id/:code" {:parameters {:path account.schema/verify-path-params}
                         :get        verify-account}]])