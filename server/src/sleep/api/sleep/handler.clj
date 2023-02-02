(ns sleep.api.sleep.handler
  (:require [sleep.api.sleep.db :as sleep.db]
            [sleep.api.sleep.schema :as sleep.schema]
            [sleep.routing.middleware :refer [wrap-authorization]]
            [sleep.routing.response :refer [ok created]]
            [sleep.routing.exception :refer [not-found]]))

(defn get-all-by-account
  [{:keys [db account-id parameters] :as request}]
  (let [sleeps (sleep.db/get-sleep-by-account-id db account-id (:query parameters))
        response (or sleeps [])]
    (ok response)))

(defn get-one
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        sleep (sleep.db/get-sleep-by-id db id)]
    (if sleep
      (ok sleep)
      (not-found request))))

(defn create
  [{:keys [db parameters account-id] :as request}]
  (let [data (assoc (:body parameters) :account-id account-id)
        response (sleep.db/create-sleep db data)]
    (created response)))

(defn update-by-ids
  [{:keys [db parameters account-id] :as request}]
  (let [ids {:account-id account-id
             :id (get-in parameters [:path :id])}
        data (:body parameters)
        response (sleep.db/update-sleep-by-id db ids data)]
    (if response
      (ok response)
      (not-found request))))

(defn delete-by-ids
  [{:keys [db parameters account-id] :as request}]
  (let [ids {:account-id account-id
             :id (get-in parameters [:path :id])}
        response (sleep.db/delete-sleep-by-id db ids)]
    (if response
      (ok response)
      (not-found request))))

(def routes
  ["/sleep" {:middleware [wrap-authorization]}
   ["" {:get {:parameters {:query sleep.schema/get-all-query}
              :handler get-all-by-account}
        :post {:parameters {:body sleep.schema/create-body}
               :handler create}}]
   ["/:id" {:parameters {:path [:map [:id uuid?]]}
            :get get-one
            :put {:parameters {:body sleep.schema/update-body}
                  :handler update-by-ids}
            :delete delete-by-ids}]])