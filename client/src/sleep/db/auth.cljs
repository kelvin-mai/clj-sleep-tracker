(ns sleep.db.auth
  (:require [re-frame.core :as rf]
            [sleep.db.ui :as ui]))

(def initial-state
  {::auth {}})

(rf/reg-event-fx
 ::check-identity
 [(rf/inject-cofx :local-storage :account/token)]
 (fn [cofx]
   (let [token (:account/token cofx)]
     {:fx [[:dispatch [:http {:url "/api/account"
                              :headers {"Authorization" (str "Bearer " token)}
                              :on-success [::auth-success]
                              :on-failure [:logout]}]]]})))

(rf/reg-event-fx
 ::login
 (fn [_ [_ data]]
   {:fx [[:dispatch [::ui/set-dialog :loading]]
         [:dispatch [:http {:url "/api/account/login"
                            :method :post
                            :data data
                            :on-success [::auth-success]
                            :on-failure [:http-failure]}]]]}))

(rf/reg-event-fx
 ::register
 (fn [_ [_ data]]
   {:fx [[:dispatch [:http {:url "/api/register"
                            :method :post
                            :data data
                            :on-success [::auth-success]
                            :on-failure [:http-failure]}]]]}))
(rf/reg-event-fx
 ::auth-success
 (fn [{:keys [db]} [_ {:keys [data]}]]
   {:db (assoc-in db [::auth :account] data)
    :fx [[:dispatch [::ui/close-dialog]]
         [:set-local-storage [:account/token (:account/token data)]]]}))

(rf/reg-event-fx
 ::logout
 (fn [{:keys [db]}]
   {:db (assoc-in db [::auth :account] nil)
    :fx [[:dispatch [::ui/close-dialog]]
         [:set-local-storage [:account/token nil]]]}))

(rf/reg-sub
 ::account
 (fn [db]
   (get-in db [::auth :account])))