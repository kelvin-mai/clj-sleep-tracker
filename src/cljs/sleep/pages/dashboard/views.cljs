(ns sleep.pages.dashboard.views
  (:require [sleep.pages.dashboard.components :refer [stat-card
                                                      history-card]]))

(defn dashboard-view []
  [:div {:class "container mx-auto py-10"}
   [:h1 {:class "text-4xl font-bold mb-10"}
    "Sleep Tracker Dashboard"]
   [:div {:class "grid gap-4 md:grid-cols-2 lg:grid-cols-3"}
    (into [:<>]
          (map-indexed (fn [i p]
                         ^{:key i}
                         [stat-card {:title (:title p)
                                     :amount (:amount p)
                                     :description (:description p)}])
                       [{:title "Average Sleep Duration"
                         :amount "7.5 hours"
                         :description "+0.2 hours from last month"}
                        {:title "Average Sleep Quality"
                         :amount "3.8/5"
                         :description "+0.3 from last month"}
                        {:title "Total Sleep Entries"
                         :amount "30"
                         :description "+5 from last month"}]))]
   [history-card]])

(def route
  ["dashboard" {:name ::dashboard
                :view dashboard-view
                :link-text "Dashboard"
                :controllers []}])