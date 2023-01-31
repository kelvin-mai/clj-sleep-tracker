(ns sleep.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [sleep.db]))

(defn app []
  [:<> [:h1 {:class "p-4 mx-auto capitalize"} "hello world"]
   [:button {:on-click #(rf/dispatch [:increase])} "+"]
   [:button {:on-click #(rf/dispatch [:decrease])} "-"]])

(defn ^:dev/after-load reload []
  (rf/clear-subscription-cache!)
  (rdom/render [app]
               (.getElementById js/document "app"))
  (js/console.log "reloaded"))

(defn ^:export init []
  (js/console.log "application starting")
  (rf/dispatch-sync [:initialize-db])
  (reload))