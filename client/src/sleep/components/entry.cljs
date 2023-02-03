(ns sleep.components.entry
  (:require [sleep.components.common :refer [form-input button-class]]
            [re-frame.core :as rf]
            [sleep.db.sleep :as sleep]))

(defn entry-form []
  (let [{:keys [sleep-date
                start-time
                end-time]
         :as form-state} @(rf/subscribe [::sleep/entry-form])]
    [:form {:class "mt-4"
            :on-submit (fn [e]
                         (.preventDefault e)
                         (rf/dispatch [::sleep/submit-entry-form form-state]))}
     [form-input {:id "date"
                  :label "Date"
                  :type "date"
                  :value sleep-date
                  :required? true
                  :on-change #(rf/dispatch [::sleep/set-entry-form :sleep-date
                                            (.. % -target -value)])}]
     [form-input {:id "start-time"
                  :label "Start Time"
                  :type "time"
                  :value start-time
                  :required? true
                  :on-change #(rf/dispatch [::sleep/set-entry-form :start-time
                                            (.. % -target -value)])}]
     [form-input {:id "end-time"
                  :label "End Time"
                  :type "time"
                  :value end-time
                  :required? true
                  :on-change #(rf/dispatch [::sleep/set-entry-form :end-time
                                            (.. % -target -value)])}]
     [:button {:class (str button-class " bg-indigo-500 hover:bg-indigo-600 text-white")
               :type "submit"}
      "Confirm"]]))