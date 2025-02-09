(ns sleep.api.accounts.handler
  (:require [sleep.api.accounts.db :as accounts.db]
            [sleep.api.accounts.schema :as accounts.schema]
            [sleep.api.accounts.utils :refer [password-match?
                                              generate-access-token]]
            [sleep.router.middleware :refer [wrap-authorization]]
            [sleep.router.response :as response]
            [sleep.router.exception :as exception]))

(defn register
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret]} env
        data                    (:body parameters)
        account                 (accounts.db/create-account db data)]
    (response/created (-> account
                          (dissoc :accounts/password)
                          (assoc :accounts/access-token
                                 (generate-access-token (:accounts/email account)
                                                        jwt-secret))))))

(defn login
  [{:keys [parameters env]
    :as   request}]
  (let [{:keys [db
                jwt-secret]}  env
        {:keys [email
                password]} (:body parameters)
        account                  (accounts.db/get-account-by-email db email)
        account                  (password-match? account password)]
    (if account
      (response/ok (-> account
                       (dissoc :accounts/password)
                       (assoc :accounts/access-token
                              (generate-access-token (:accounts/email account)
                                                     jwt-secret))))
      (exception/response 403 "Invalid credentials" request))))

(defn check-identity
  [{:keys [identity]
    :as   request}]
  (let []
    (response/ok identity)))

(def routes
  ["/accounts"
   ["/" {:get {:middleware [wrap-authorization]
               :handler    check-identity}}]
   ["/register" {:post {:parameters {:body accounts.schema/register-body}
                        :handler    register}}]
   ["/login" {:post {:parameters {:body accounts.schema/register-body}
                     :handler    login}}]])