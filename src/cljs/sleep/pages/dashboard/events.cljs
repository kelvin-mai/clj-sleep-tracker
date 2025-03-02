(ns sleep.pages.dashboard.events
  (:require [re-frame.core :as rf]))

(def initial-state
  {:dashboard {:loading false}})

(rf/reg-event-fx
 ::initialize-dashboard
 [(rf/inject-cofx :local-storage :token/access-token)]
 (fn [{:keys [db]
       :token/keys [access-token]}
      _]
   (let [{:keys [start-date
                 end-date]} (get-in db [:current-route :parameters])]
     (if access-token
       {:fx [[:dispatch [::fetch-sleeps {:start-date start-date
                                         :end-date end-date}]]]}
       {:fx [[:navigate! :sleep.pages.auth.views/login]]}))))

(rf/reg-event-fx
 ::fetch-sleeps
 [(rf/inject-cofx :local-storage :token/access-token)]
 (fn [{:keys [db]
       :token/keys [access-token]}
      {:keys [start-date end-date]}]
   {:db (assoc-in db [:dashboard :loading] true)
    :fx [[:dispatch [:http {:url (str "/api/sleep?"
                                      (when start-date (str "start-date=" start-date))
                                      (when (and start-date end-date) "&")
                                      (when end-date (str "end-date=" end-date)))
                            :headers {:authorization (str "Bearer " access-token)}
                            :method :get
                            :on-success [::fetch-sleeps-success]
                            :on-failure [:http-failure]}]]]}))

(rf/reg-event-db
 ::fetch-sleeps-success
 (fn [db [_ {:keys [data]}]]
   (-> db
       (assoc-in [:dashboard :loading] false)
       (assoc-in [:dashboard :sleeps] data))))