(ns sleep.pages.router
  (:require [re-frame.core :as rf]
            [reitit.core :as r-core]
            [reitit.frontend :as r]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.malli :as rcm]
            [sleep.utils :refer [href]]
            [sleep.pages.auth.views :as auth]
            [sleep.pages.dashboard.views :as dashboard]))

(defn home-page []
  [:div
   [:h1 "Welcome to Sleep"]
   [:p "This is a simple sleep tracking app."]
   [:a {:href "/auth"} "Sign Up"]
   [:a {:href "/dashboard"} "Dashboard"]])

(defn on-navigate [new-match]
  (rf/dispatch [:navigated new-match]))

(defn nav [{:keys [router current-route]}]
  [:ul {:class "flex justify-around"}
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
    auth/routes
    dashboard/route]
   {:data {:coercion rcm/coercion}}))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))

(defn router-component []
  (let [current-route @(rf/subscribe [:current-route])]
    [:div
     [:nav (nav {:router router :current-route current-route})]
     (when-let [view (get-in current-route [:data :view])]
       [view])]))