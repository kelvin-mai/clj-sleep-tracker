(ns sleep.db.sleep
  (:require [re-frame.core :as rf]
            [sleep.db.auth :as auth]
            [sleep.db.ui :as ui]
            [sleep.utils :refer [dayjs-format]]))

(def initial-form
  {:sleep-date nil
   :start-time nil
   :end-time nil
   :method nil})

(def initial-state
  {::sleep {:form initial-form
            :data []}})

(defn format-sleep [sleep]
  (let [format-date dayjs-format
        format-time #(dayjs-format % "hh:mm a")]
    (-> sleep
        (update :sleep/sleep-date format-date)
        (update :sleep/start-time format-time)
        (update :sleep/end-time format-time))))

(rf/reg-event-fx
 ::get-sleeps
 (fn [{:keys [db]} [_ dates]]
   (let [token (get-in db [::auth/auth :account :account/token])
         dates (if dates
                 (js->clj dates)
                 (get-in db [::sleep :dates]))
         {:strs [startDate endDate]} dates
         query-params (when (and startDate endDate)
                        (str "start-date="
                             (dayjs-format startDate)
                             "&end-date="
                             (dayjs-format endDate)))
         url (str "/api/sleep?" query-params)]
     {:db (assoc-in db [::sleep :dates] dates)
      :fx [[:dispatch [:http {:url url
                              :method :get
                              :headers {"Authorization" (str "Bearer " token)}
                              :on-success [::get-sleeps-success]
                              :on-failure [:http-failure]}]]
           [:dispatch [::ui/set-dialog :loading]]]})))

(rf/reg-event-fx
 ::open-entry-form
 (fn [_ [_ date]]
   {:fx [[:dispatch [::ui/set-dialog :entry]]
         (when date [:dispatch [::select-sleep-date date]])]}))

(rf/reg-event-fx
 ::select-sleep-date
 (fn [{:keys [db]} [_ date]]
   (let [token (get-in db [::auth/auth :account :account/token])
         url (str "/api/sleep/" date)]
     {:fx [[:dispatch [:http {:url url
                              :method :get
                              :headers {"Authorization" (str "Bearer " token)}
                              :on-success [::select-sleep-date-success date]
                              :on-failure [::select-sleep-date-failure date]}]]]})))

(rf/reg-event-fx
 ::get-sleeps-success
 (fn [{:keys [db]} [_ {:keys [data]}]]
   {:db (assoc-in db [::sleep :data] (map format-sleep data))
    :fx [[:dispatch [::ui/close-dialog]]]}))

(rf/reg-event-db
 ::select-sleep-date-success
 (fn [db [_ date _]]
   (-> db
       (assoc-in [::sleep :form :sleep-date] date)
       (assoc-in [::sleep :form :method] :put))))

(rf/reg-event-db
 ::select-sleep-date-failure
 (fn [db [_ date _]]
   (-> db
       (assoc-in [::sleep :form :sleep-date] date)
       (assoc-in [::sleep :form :method] :post))))

(rf/reg-event-db
 ::set-entry-form
 (fn [db [_ k v]]
   (assoc-in db [::sleep :form k] v)))

(rf/reg-event-fx
 ::submit-entry-form
 (fn [{:keys [db]} [_ {:keys [method] :as data}]]
   (let [token (get-in db [::auth/auth :account :account/token])
         data (select-keys data [:sleep-date :start-time :end-time])
         url "/api/sleep"
         url (if (= method :put)
               (str url "/" (:sleep-date data))
               url)]
     {:fx [[:dispatch [:http {:url url
                              :method method
                              :headers {"Authorization" (str "Bearer " token)}
                              :data data
                              :on-success [::submit-entry-form-success]
                              :on-failure [:http-failure]}]]]})))

(rf/reg-event-fx
 ::submit-entry-form-success
 (fn [_ [_ _]]
   {:fx [[:dispatch [::ui/set-dialog :confirm-entry]]]}))

(rf/reg-event-fx
 ::delete-sleep-date
 (fn [{:keys [db]} [_ date]]
   (let [token (get-in db [::auth/auth :account :account/token])
         url (str "/api/sleep/" date)]
     {:fx [[:dispatch [:http {:url url
                              :method :delete
                              :headers {"Authorization" (str "Bearer " token)}
                              :on-success [::delete-sleep-date-success]
                              :on-failure [:http-failure]}]]]})))

(rf/reg-event-fx
 ::delete-sleep-date-success
 (fn [_ _]
   {:fx [[:dispatch [::ui/set-dialog :confirm-delete]]]}))

(rf/reg-event-fx
 ::close-dialog
 (fn [{:keys [db]} [_ _]]
   {:db (assoc-in db [::sleep :form] initial-form)
    :fx [[:dispatch [::get-sleeps]]]}))

(rf/reg-sub
 ::entry-form
 (fn [db]
   (get-in db [::sleep :form])))

(rf/reg-sub
 ::sleep-data
 (fn [db]
   (get-in db [::sleep :data])))