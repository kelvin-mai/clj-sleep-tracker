(ns sleep.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [sleep.db]))

(defn app []
  [:h1 "hello world"])

(defn reload []
  (rf/clear-subscription-cache!)
  (rdom/render [app]
               (.getElementById js/document "app"))
  (js/console.log "reloaded"))

(defn init []
  (js/console.log "application starting")
  (rf/dispatch-sync [:initialize-db])
  (reload))

(comment
  (rf/dispatch [:increase]))
