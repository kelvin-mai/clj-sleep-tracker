(ns sleep.api.account.handler
  (:require [sleep.api.account.db :as account.db]
            [sleep.api.account.schema :as account.schema]
            [sleep.utils.auth :as auth]
            [sleep.routing.response :refer [ok created]]
            [sleep.routing.middleware :refer [wrap-authorization]]
            [sleep.routing.exception :as exception]))

(defn register
  [{:keys [db parameters jwt-secret]}]
  (let [data (:body parameters)
        account (account.db/create-account db data)
        response (auth/account->response account jwt-secret)]
    (created response)))

(defn login
  [{:keys [db parameters jwt-secret] :as request}]
  (let [{:keys [username password]} (:body parameters)
        account (account.db/get-by-username db username)
        account (auth/password-match? account password)
        response (when account
                   (auth/account->response account jwt-secret))]
    (if response
      (ok response)
      (exception/response 403 "Invalid credentials" request))))

(defn check-identity
  [{:keys [db identity jwt-secret] :as request}]
  (let [{:account/keys [username]} identity
        account (account.db/get-by-username db username)
        response (when account
                   (auth/account->response account jwt-secret))]
    (if account
      (ok response)
      (exception/response 403 "Malformed token" request))))

(def routes
  ["/account"
   ["" {:get {:middleware [wrap-authorization]
              :handler check-identity}}]
   ["/login" {:post {:parameters {:body account.schema/auth-body}
                     :handler login}}]
   ["/register" {:post {:parameters {:body account.schema/auth-body}
                        :handler register}}]])
