(ns sleep.components.stats
  (:require ["react-tailwindcss-datepicker" :as DatePicker]
            [reagent.core :as r]))

(defn stats-graph []
  [:div
   [:h2 "Sleep Duration"]])

(defn stats-table []
  [:div
   [:h2 "Sleep Stats"]])

(defn stats []
  (let [state (r/atom (clj->js {:startDate nil
                                :endDate nil}))]
    (fn []
      [:<>
       [:> DatePicker {:primary-color "indigo"
                       :value @state
                       :on-change #(reset! state %)
                       :show-shortcuts true}]
       [:div
        [stats-graph]
        [stats-table]]])))