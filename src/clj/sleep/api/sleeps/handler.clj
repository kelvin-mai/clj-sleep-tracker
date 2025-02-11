(ns sleep.api.sleeps.handler
  (:require [sleep.api.sleeps.db :as sleeps.db]
            [sleep.api.sleeps.schema :as sleeps.schema]
            [sleep.router.response :as response]
            [sleep.router.exception :as exception]
            [sleep.router.middleware :refer [wrap-authorization]]))

(defn get-sleeps
  [{:keys [env identity]}]
  (let [db         (:db env)
        account-id (:sub identity)
        sleeps     (sleeps.db/get-sleeps db {:account-id account-id})]
    (response/ok sleeps)))

(defn create-sleep
  [{:keys [parameters env identity]}]
  (let [{:keys [db]} env
        data         (assoc (:body parameters)
                            :account-id (:sub identity))
        sleep        (sleeps.db/create-sleep! db data)]
    (response/created sleep)))

(defn get-sleep
  [{:keys [parameters env identity]
    :as   request}]
  (let [db    (:db env)
        ident {:account-id (:sub identity)
               :date       (get-in parameters [:path :date])}
        sleep (sleeps.db/get-sleep-by-date db ident)]
    (if sleep
      (response/ok sleep)
      (exception/response 404 "Resource not found" request))))

(defn update-sleep
  [{:keys [parameters env]}]
  (let [db    (:db env)
        ident {:account-id (:sub identity)
               :date       (get-in parameters [:path :date])}
        data  (:body parameters)
        sleep (sleeps.db/update-sleep! db ident data)]
    (response/ok sleep)))

(defn delete-sleep
  [{:keys [parameters env]}]
  (let [{:keys [db]} env
        ident        {:account-id (:sub identity)
                      :date       (get-in parameters [:path :date])}
        sleep        (sleeps.db/delete-sleep! db ident)]
    (response/ok sleep)))

(def routes
  ["/sleeps" {:middleware [wrap-authorization]}
   ["" {:get  get-sleeps
        :post {:parameters {:body sleeps.schema/create-body}
               :handler    create-sleep}}]
   ["/:date" {:parameters {:path sleeps.schema/path-param}
              :get        get-sleep
              :put        {:parameters {:body sleeps.schema/update-body}
                           :handler    update-sleep}
              :delete     delete-sleep}]])