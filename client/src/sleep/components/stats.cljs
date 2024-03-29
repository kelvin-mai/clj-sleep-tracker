(ns sleep.components.stats
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            ["dayjs" :as dayjs]
            ["@heroicons/react/24/solid" :refer [PencilSquareIcon
                                                 TrashIcon]]
            ["react-tailwindcss-datepicker" :as DatePicker]
            ["recharts" :refer [Area
                                AreaChart
                                CartesianGrid
                                ResponsiveContainer
                                XAxis
                                YAxis
                                Tooltip]]
            [sleep.db.sleep :as sleep]))

(defn stats-chart [data]
  [:> ResponsiveContainer {:width "100%"
                           :height 300}
   [:> AreaChart {:data data
                  :margin {:top 10
                           :right 30
                           :left 0
                           :bottom 0}}
    [:> CartesianGrid {:strokeDasharray "3 3"}]
    [:> XAxis {:dataKey :sleep/sleep-date}]
    [:> YAxis]
    [:> Tooltip]
    [:> Area {:type "monotone"
              :dataKey :sleep/duration
              :stroke "#6366f1"
              :fill "#6366f1"}]]])

(defn stats-table [data]
  [:table {:class "w-full shadow-md bg-white border border-collapse rounded-md"}
   [:thead
    [:tr
     (map
      (fn [head]
        ^{:key head}
        [:th {:class "border text-center px-4 bg-indigo-500 text-white"}
         head])
      ["Date" "Start Time" "End Time" "Duration" ""])]]
   [:tbody
    (map
     (fn [row]
       ^{:key (:sleep/sleep-date row)}
       [:tr
        (map (fn [cell]
               ^{:key (str (:sleep/sleep-date row) "_" cell)}
               [:td {:class "border text-right px-4 bg-indigo-50 py-2"}
                (get row cell)])
             [:sleep/sleep-date :sleep/start-time :sleep/end-time :sleep/duration])
        [:td {:class "border px-4 bg-indigo-50 py-2"}
         [:div {:class "flex justify-around"}
          [:button {:on-click #(rf/dispatch [::sleep/open-entry-form (:sleep/sleep-date row)])}
           [:> PencilSquareIcon {:class "fill-teal-500 w-4 h-4"}]]
          [:button {:on-click #(rf/dispatch [::sleep/delete-sleep-date (:sleep/sleep-date row)])}
           [:> TrashIcon {:class "fill-rose-400 w-4 h-4"}]]]]])
     data)]])

(defn stats []
  (let [state (r/atom (clj->js {:startDate nil
                                :endDate nil}))]
    (fn []
      (let [data @(rf/subscribe [::sleep/sleep-data])]
        [:div {:class "p-2 mt-2"}
         [:> DatePicker {:primary-color "indigo"
                         :value @state
                         :on-change #(do (reset! state %)
                                         (rf/dispatch [::sleep/get-sleeps %]))
                         :placeholder "Select date range"
                         :max-date (dayjs)
                         :show-shortcuts true}]
         [:div {:class "my-4"}
          (if (empty? data)
            [:h2 {:class ""} "Please select date range to see sleep data"]
            [:<>
             [stats-table data]
             [stats-chart data]])]]))))