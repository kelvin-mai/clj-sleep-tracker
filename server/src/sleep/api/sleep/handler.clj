(ns sleep.api.sleep.handler
  (:require [sleep.api.sleep.db :as sleep.db]
            [sleep.api.sleep.schema :as sleep.schema]
            [sleep.routing.middleware :refer [wrap-authorization]]
            [sleep.routing.response :refer [ok created]]
            [sleep.routing.exception :refer [not-found]]))

(defn get-all-by-account
  [{:keys [db account-id parameters]}]
  (let [sleeps (sleep.db/get-sleep-by-account-id db account-id (:query parameters))
        response (or sleeps [])]
    (ok response)))

(defn get-by-date
  [{:keys [db parameters account-id] :as request}]
  (let [constraints {:account-id account-id
                     :date (get-in parameters [:path :date])}
        sleep (sleep.db/get-sleep-by-date db constraints)]
    (if sleep
      (ok sleep)
      (not-found request))))

(defn create
  [{:keys [db parameters account-id]}]
  (let [data (assoc (:body parameters) :account-id account-id)
        response (sleep.db/create-sleep db data)]
    (created response)))

(defn update-by-date
  [{:keys [db parameters account-id] :as request}]
  (let [constraints {:account-id account-id
                     :date (get-in parameters [:path :date])}
        data (:body parameters)
        response (sleep.db/update-sleep-by-date db constraints data)]
    (if response
      (ok response)
      (not-found request))))

(defn delete-by-date
  [{:keys [db parameters account-id] :as request}]
  (let [constraints {:account-id account-id
                     :date (get-in parameters [:path :date])}
        response (sleep.db/delete-sleep-by-date db constraints)]
    (if response
      (ok response)
      (not-found request))))

(def routes
  ["/sleep" {:middleware [wrap-authorization]}
   ["" {:get {:parameters {:query sleep.schema/get-all-query}
              :handler get-all-by-account}
        :post {:parameters {:body sleep.schema/create-body}
               :handler create}}]
   ["/:date" {:parameters {:path sleep.schema/path-param}
              :get get-by-date
              :put {:parameters {:body sleep.schema/update-body}
                    :handler update-by-date}
              :delete delete-by-date}]])
