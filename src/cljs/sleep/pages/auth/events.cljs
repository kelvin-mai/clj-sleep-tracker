(ns sleep.pages.auth.events
  (:require [re-frame.core :as rf]))

(def initial-state
  {::auth {:account nil}})

(rf/reg-event-fx
 ::login
 (fn [_ [_ data]]
   {:fx [[:dispatch [:http {:url "/api/account/login"
                            :method :post
                            :data data
                            :on-success [::auth-success]
                            :on-failure [:http-failure]}]]]}))

(rf/reg-event-fx
 ::register
 (fn [_ [_ data]]
   {:fx [[:dispatch [:http {:url "/api/account/register"
                            :method :post
                            :data data
                            :on-success [::auth-success]
                            :on-failure [:http-failure]}]]]}))
(rf/reg-event-fx
 ::auth-success
 (fn [{:keys [db]} [_ {:keys [data] :as response}]]
   (js/console.log ::auth-succes response)
   {:db (assoc-in db [::auth :account] data)
    :fx [[:set-local-storage [:token/access-token (:token/access-token data)]]
         [:set-local-storage [:token/refresh-token (:token/refresh-token data)]]]}))

(rf/reg-event-fx
 ::logout
 (fn [{:keys [db]} _]
   {:db (assoc-in db [::auth :account] nil)
    :fx [[:set-local-storage [:account/token nil]]]}))