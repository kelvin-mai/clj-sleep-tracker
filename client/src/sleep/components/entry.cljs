(ns sleep.components.entry
  (:require [re-frame.core :as rf]
            [sleep.db.sleep :as sleep]
            [sleep.components.common :refer [form-input button-class]]))

(def confirm-entry-labels
  {:sleep-date "Sleep Date"
   :start-time "Start Time"
   :end-time "End Time"})

(defn confirm-entry []
  (let [{:keys [method]
         :as entry} @(rf/subscribe [::sleep/entry-form])]
    [:div {:class "mt-2"}
     [:h4 {:class "text-center font-bold"}
      (str "Data " (if (= method :post)
                     "created"
                     "updated"))]
     [:div {:class "my-2"}
      (map (fn [k]
             ^{:key k}
             [:p
              [:span {:class "font-bold"} (str (get confirm-entry-labels k) ": ")]
              (get entry k)])
           [:sleep-date :start-time :end-time])]
     [:button {:class (str button-class " bg-indigo-500 text-white hover:bg-indigo-600")
               :on-click #(rf/dispatch [::sleep/close-dialog])}
      "Confirm"]]))

(defn entry-form []
  (let [{:keys [sleep-date
                start-time
                end-time]
         :as form-state} @(rf/subscribe [::sleep/entry-form])
        date-selected? (nil? sleep-date)]
    [:form {:class "mt-4"
            :on-submit (fn [e]
                         (.preventDefault e)
                         (rf/dispatch [::sleep/submit-entry-form form-state]))}
     [form-input {:id "date"
                  :label "Date"
                  :type "date"
                  :value sleep-date
                  :required? true
                  :on-change #(rf/dispatch [::sleep/select-sleep-date
                                            (.. % -target -value)])}]
     [form-input {:id "start-time"
                  :label "Start Time"
                  :type "time"
                  :value start-time
                  :required? true
                  :disabled? date-selected?
                  :on-change #(rf/dispatch [::sleep/set-entry-form :start-time
                                            (.. % -target -value)])}]
     [form-input {:id "end-time"
                  :label "End Time"
                  :type "time"
                  :value end-time
                  :required? true
                  :disabled? date-selected?
                  :on-change #(rf/dispatch [::sleep/set-entry-form :end-time
                                            (.. % -target -value)])}]
     [:button {:class (str button-class " bg-indigo-500 hover:bg-indigo-600 text-white")
               :type "submit"}
      "Confirm"]]))