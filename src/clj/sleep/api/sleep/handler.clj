(ns sleep.api.sleep.handler
  (:require [sleep.api.sleep.db :as sleep.db]
            [sleep.api.sleep.schema :as sleep.schema]
            [sleep.router.response :as response]
            [sleep.router.middleware :refer [wrap-authorization]]))

(defn get-sleeps
  [{:keys [env identity]}]
  (let [db         (:db env)
        account-id (:sub identity)
        sleeps     (sleep.db/get-sleeps db account-id)]
    (response/ok sleeps)))

(defn create-sleep
  [{:keys [parameters env identity]}]
  (let [{:keys [db]} env
        data         (assoc (:body parameters)
                            :account-id (:sub identity))
        sleep        (sleep.db/create-sleep! db data)]
    (response/created sleep)))

(defn get-sleep
  [{:keys [parameters env identity]}]
  (let [db    (:db env)
        ident {:account-id (:sub identity)
               :date       (get-in parameters [:path :date])}
        sleep (sleep.db/get-sleep-by-date db ident)]
    (if sleep
      (response/ok sleep)
      (throw (ex-info "Resource not found"
                      {:status 404
                       :type ::not-found})))))


(defn update-sleep
  [{:keys [parameters env identity]}]
  (let [db    (:db env)
        ident {:account-id (:sub identity)
               :date       (get-in parameters [:path :date])}
        data  (:body parameters)
        sleep (sleep.db/update-sleep! db ident data)]
    (response/ok sleep)))

(defn delete-sleep
  [{:keys [parameters env identity]}]
  (let [{:keys [db]} env
        ident        {:account-id (:sub identity)
                      :date       (get-in parameters [:path :date])}
        sleep        (sleep.db/delete-sleep! db ident)]
    (response/ok sleep)))

(def routes
  ["/sleep" {:middleware [wrap-authorization]}
   ["" {:get  get-sleeps
        :post {:parameters {:body sleep.schema/create-body}
               :handler    create-sleep}}]
   ["/:date" {:parameters {:path sleep.schema/date-path-param}
              :get        get-sleep
              :put        {:parameters {:body sleep.schema/update-body}
                           :handler    update-sleep}
              :delete     delete-sleep}]])