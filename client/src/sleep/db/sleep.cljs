(ns sleep.db.sleep
  (:require [re-frame.core :as rf]
            [sleep.db.auth :as auth]
            [sleep.db.ui :as ui]
            [sleep.utils :refer [dayjs-format]]))

(def initial-state
  {::sleep {:form {:sleep-date nil
                   :start-time nil
                   :end-time nil}
            :data []}})

(defn format-sleep [sleep]
  (let [format-date #(dayjs-format % "MM-DD")
        format-time #(dayjs-format % "hh:mm a")]
    (-> sleep
        (update :sleep/sleep-date format-date)
        (update :sleep/start-time format-time)
        (update :sleep/end-time format-time))))

(rf/reg-event-fx
 ::get-sleeps
 (fn [{:keys [db]} [_ dates]]
   (let [token (get-in db [::auth/auth :account :account/token])
         {:strs [startDate endDate]} (js->clj dates)
         query-params (when (and startDate endDate)
                        (str "start-date="
                             (dayjs-format startDate)
                             "&end-date="
                             (dayjs-format endDate)))
         url (str "/api/sleep?" query-params)]
     {:fx [[:dispatch [:http {:url url
                              :method :get
                              :headers {"Authorization" (str "Bearer " token)}
                              :on-success [::get-sleeps-success]
                              :on-failure [:http-failure]}]]]})))

(rf/reg-event-db
 ::get-sleeps-success
 (fn [db [_ {:keys [data]}]]
   (assoc-in db [::sleep :data] (map format-sleep data))))

(rf/reg-event-db
 ::set-entry-form
 (fn [db [_ k v]]
   (assoc-in db [::sleep :form k] v)))

(rf/reg-event-fx
 ::submit-entry-form
 (fn [{:keys [db]} [_ data]]
   (let [token (get-in db [::auth/auth :account :account/token])]
     {:fx [[:dispatch [:http {:url "/api/sleep"
                              :method :post
                              :headers {"Authorization" (str "Bearer " token)}
                              :data data
                              :on-success [::submit-entry-form-success]
                              :on-failure [:http-failure]}]]]})))

(rf/reg-event-fx
 ::submit-entry-form-success
 (fn [_ [_ response]]
   (js/console.log response)
   {:fx [[:dispatch [::ui/close-dialog]]]}))

(rf/reg-sub
 ::entry-form
 (fn [db]
   (get-in db [::sleep :form])))

(rf/reg-sub
 ::sleep-data
 (fn [db]
   (get-in db [::sleep :data])))