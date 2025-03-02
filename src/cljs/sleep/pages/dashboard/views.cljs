(ns sleep.pages.dashboard.views
  (:require [re-frame.core :as rf]
            [sleep.components.common :refer [button]]
            [sleep.utils.schema :refer [date?]]
            [sleep.pages.dashboard.components :refer [stat-card
                                                      history-card]]
            [sleep.pages.dashboard.events :as dashboard.events]
            [sleep.pages.dashboard.subs :as dashboard.subs]))

(defn dashboard-view []
  (let [loading @(rf/subscribe [::dashboard.subs/loading])]
    [:div {:class "container mx-auto py-10"}
     [:h1 {:class "text-4xl font-bold mb-10"}
      "Sleep Tracker Dashboard"]
     [button {:on-click #(rf/dispatch [::dashboard.events/fetch-sleeps])
              :disabled loading}
      "temp"]
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
     [history-card]]))

(def route
  ["dashboard" {:name ::dashboard
                :view dashboard-view
                :link-text "Dashboard"
                :parameters {:query [:map
                                     [:start-date {:optional true} date?]
                                     [:end-date {:optional true} date?]]}
                :controllers [{:start #(rf/dispatch [::dashboard.events/initialize-dashboard])
                               :stop (fn [& params] (js/console.log "Stopping Dashboard Controller" params))}]}])