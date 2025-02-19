(ns sleep.pages.router
  (:require [re-frame.core :as rf]
            [reitit.core :as r-core]
            [reitit.frontend :as r]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.malli :as rcm]
            [sleep.pages.auth.view :as auth]
            [sleep.pages.dashboard.view :as dashboard]))

(def home-page
  [:div
   [:h1 "Welcome to Sleep"]
   [:p "This is a simple sleep tracking app."]])

(defn on-navigate [new-match]
  (rf/dispatch [:navigated new-match]))

(defn href
  ([k] (href k nil nil))
  ([k params] (href k params nil))
  ([k params query] (rfe/href k params query)))

(defn nav [{:keys [router current-route]}]
  [:ul
   (for [route-name (r-core/route-names router)
         :let [route (r-core/match-by-name router route-name)
               text (-> route :data :link-text)]]
     [:li {:key route-name}
      (when (= route-name (-> current-route :data :name))
        "> ")
      [:a {:href (href route-name)} text]])])

(def router
  (r/router
   ["/"
    ["" {:name ::home
         :view home-page
         :link-text "Home"
         :controllers []}]
    auth/route
    dashboard/route]
   {:data {:coercion rcm/coercion}}))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))

(defn router-component [{:keys [router]}]
  (let [current-route @(rf/subscribe [:current-route])]
    [:div
     [:nav (nav {:router router :current-route current-route})]
     (when current-route
       (get-in current-route [:data :view]))]))