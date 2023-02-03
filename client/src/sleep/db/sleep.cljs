(ns sleep.db.sleep
  (:require [re-frame.core :as rf]
            [sleep.db.auth :as auth]
            [sleep.db.ui :as ui]))

(def initial-state
  {::sleep {:form {:sleep-date nil
                   :start-time nil
                   :end-time nil}}})

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