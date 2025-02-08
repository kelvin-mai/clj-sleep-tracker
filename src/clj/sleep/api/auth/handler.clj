(ns sleep.api.auth.handler
  (:require [sleep.api.auth.db :as auth.db]
            [sleep.api.auth.schema :as auth.schema]
            [sleep.router.response :as response]
            [sleep.router.exception :as exception]
            [sleep.utils.auth :as auth]))

(defn sanitize-user [user]
  (dissoc user :users/password))

(defn register
  [{:keys [parameters env]}]
  (println {:db         (:db env)
            :parameters parameters
            :env        env
            :jwt-secret (:jwt-secret env)})
  (let [{:keys [db
                jwt-secret]} env
        data                    (:body parameters)
        user                    (auth.db/create-user db data)]
    (response/created {:user (sanitize-user user)})))

(defn login
  [{:keys [parameters env]
    :as   request}]
  (let [{:keys [db
                jwt-secret]}  env
        {:keys [email
                password]} (:body parameters)
        user                     (auth.db/get-user-by-email db email)
        user                     (auth/password-match? user password)]
    (if user
      (response/ok (sanitize-user user))
      (exception/response 403 "Invalid credentials" request))))

(def routes
  ["/auth"
   ["/register" {:post {:parameters {:body auth.schema/register-body}
                        :handler    register}}]
   ["/login" {:post {:parameters {:body auth.schema/register-body}
                     :handler    login}}]])