(ns sleep.components.entry
  (:require [sleep.components.common :refer [form-input button-class]]
            [re-frame.core :as rf]))

(def initial-state {:form {:sleep-date nil
                           :start-time nil
                           :end-time nil}})

(rf/reg-event-db
 :set-entry-form
 (fn [db [_ k v]]
   (assoc-in db [:entry :form k] v)))

(rf/reg-event-fx
 :submit-entry-form
 (fn [{:keys [db]} [_ data]]
   (let [token (get-in db [:auth :account :account/token])]
     {:fx [[:dispatch [:http {:url "/api/sleep"
                              :method :post
                              :headers {"Authorization" (str "Bearer " token)}
                              :data data
                              :on-success [:submit-entry-form-success]
                              :on-failure [:http-failure]}]]]})))

(rf/reg-event-fx
 :submit-entry-form-success
 (fn [_ [_ response]]
   (js/console.log response)
   {:fx [[:dispatch [:close-dialog]]]}))

(rf/reg-sub
 :entry-form
 (fn [db]
   (get-in db [:entry :form])))

(defn entry-form []
  (let [{:keys [sleep-date
                start-time
                end-time]
         :as form-state} @(rf/subscribe [:entry-form])]
    [:form {:class "mt-4"
            :on-submit (fn [e]
                         (.preventDefault e)
                         (rf/dispatch [:submit-entry-form form-state]))}
     [form-input {:id "date"
                  :label "Date"
                  :type "date"
                  :value sleep-date
                  :required? true
                  :on-change #(rf/dispatch [:set-entry-form :sleep-date
                                            (.. % -target -value)])}]
     [form-input {:id "start-time"
                  :label "Start Time"
                  :type "time"
                  :value start-time
                  :required? true
                  :on-change #(rf/dispatch [:set-entry-form :start-time
                                            (.. % -target -value)])}]
     [form-input {:id "end-time"
                  :label "End Time"
                  :type "time"
                  :value end-time
                  :required? true
                  :on-change #(rf/dispatch [:set-entry-form :end-time
                                            (.. % -target -value)])}]
     [:button {:class (str button-class " bg-indigo-500 hover:bg-indigo-600 text-white")
               :type "submit"}
      "Confirm"]]))