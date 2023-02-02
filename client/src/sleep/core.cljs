(ns sleep.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [sleep.effects]
            [sleep.events]
            [sleep.db]
            [sleep.components.core :refer [app]]))

(defn ^:dev/after-load reload []
  (rf/clear-subscription-cache!)
  (rdom/render [app]
               (.getElementById js/document "app"))
  (js/console.log "reloaded"))

(defn ^:export init []
  (js/console.log "application starting")
  (rf/dispatch-sync [:initialize-db])
  (js/console.log "Begin rendering")
  (rf/dispatch [:check-identity])
  (reload))