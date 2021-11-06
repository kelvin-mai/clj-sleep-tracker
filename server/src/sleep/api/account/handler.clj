(ns sleep.api.account.handler
  (:require [sleep.api.account.db :as account.db]
            [sleep.api.account.schema :as account.schema]
            [sleep.utils.auth :as auth]
            [sleep.routing.response :refer [ok created]]
            [sleep.routing.exception :as exception]))

(defn register
  [{:keys [db parameters jwt-secret]}]
  (let [data (:body parameters)
        account (account.db/create-account db data)
        response (auth/account->response account jwt-secret)]
    (created response)))

(defn login
  [{:keys [db parameters jwt-secret] :as request}]
  (let [username (get-in parameters [:body :username])
        account (account.db/get-by-username db username)
        response (when account
                   (auth/account->response account jwt-secret))]
    (if account
      (ok response)
      (exception/response 403 "Invalid credentials" request))))

(def routes
  ["/account"
   ["/login" {:post {:parameters {:body account.schema/login-body}
                     :handler login}}]
   ["/register" {:post {:parameters {:body account.schema/register-body}
                        :handler register}}]])

