(ns sleep.core
  (:require [reagent.dom.client :as rdom]
            [re-frame.core :as rf]
            [sleep.pages.router :refer [init-routes! router-component router]]
            sleep.db.core))

(defonce root
  (rdom/create-root (.getElementById js/document "app")))

(defn ^:dev/after-load reload! []
  (rf/clear-subscription-cache!)
  (init-routes!)
  (rdom/render root [router-component {:router router}])
  (js/console.log "reloaded"))

(defn ^:export init []
  (js/console.log "application starting")
  (rf/dispatch-sync [:initialize-db])
  (reload!))