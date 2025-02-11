(ns sleep.api.accounts.handler
  (:require [sleep.api.accounts.db :as accounts.db]
            [sleep.api.accounts.schema :as accounts.schema]
            [sleep.api.accounts.utils :refer [password-match?
                                              generate-tokens!
                                              generate-access-token]]
            [sleep.router.middleware :refer [wrap-authorization]]
            [sleep.router.response :as response]
            [sleep.router.exception :as exception]
            [sleep.utils.maps :refer [map->ns-map]]))

(defn register
  [{:keys [parameters env]}]
  (let [{:keys [db
                jwt-secret]} env
        data                    (:body parameters)
        account                 (accounts.db/create-account! db data)]
    (response/created (-> account
                          (dissoc :accounts/password)
                          (merge (generate-tokens! db
                                                   (:accounts/id account)
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
                       (merge (generate-tokens! db
                                                (:accounts/id account)
                                                jwt-secret))))
      (exception/response 403 "Invalid credentials" request))))

(defn check-identity
  [{:keys [identity env]
    :as   request}]
  (let [{:keys [db]} env
        id           (:id identity)
        account      (accounts.db/get-account-by-id db id)]
    (if account
      (response/ok (-> account
                       (dissoc :accounts/password)
                       (merge (map->ns-map "claims" identity))))
      (exception/response 403 "Invalid credentials" request))))

(defn logout
  [{:keys [identity env]}]
  (let [{:keys [db]} env
        jti          (:jti identity)
        _            (accounts.db/delete-refresh-token! db jti)]
    (response/ok {:success true})))

(defn refresh-access-token
  [{:keys [identity parameters env]
    :as   request}]
  (let [{:keys [db
                jwt-secret]} env
        {:keys [jti
                sub]}       identity
        token                   (get-in parameters [:body :refresh-token])
        refresh-token           (accounts.db/get-refresh-token-by-token-and-jti db jti token)]
    (if refresh-token
      (response/ok (assoc refresh-token
                          :refresh-tokens/access-token (generate-access-token jti sub jwt-secret)))
      (exception/response 403 "Invalid credentials" request))))

(def routes
  ["/accounts"
   ["/" {:middleware [wrap-authorization]
         :get        check-identity
         :delete     logout}]
   ["/register" {:post {:parameters {:body accounts.schema/register-body}
                        :handler    register}}]
   ["/login" {:post {:parameters {:body accounts.schema/login-body}
                     :handler    login}}]
   ["/refresh" {:middleware [wrap-authorization]
                :post       {:parameters {:body accounts.schema/refresh-access-token-body}
                             :handler    refresh-access-token}}]])