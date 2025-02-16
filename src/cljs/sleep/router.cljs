(ns sleep.router
  (:require [re-frame.core :as rf]
            [reitit.frontend :as r]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.malli :as rcm]))

(def home-page
  [:div
   [:h1 "Welcome to Sleep"]
   [:p "This is a simple sleep tracking app."]])

(defn on-navigate [new-match]
  (rf/dispatch [:navigated new-match]))

(def router
  (r/router
   [["/" {:name "home"
          :view home-page
          :link-text "Home"
          :controllers []}]]
   {:data {:coercion rcm/coercion}}))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment true}))

(defn router-component [{:keys [router]}]
  (let [current-route @(rf/subscribe [:current-route])]
    [:div
     (when current-route
       (get-in current-route [:data :view]))]))