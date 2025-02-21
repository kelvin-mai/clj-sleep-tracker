(ns sleep.pages.auth.events
  (:require [re-frame.core :as rf]
            [malli.core :as m]
            [malli.error :as me]
            [sleep.utils.schema :refer [email?
                                        non-blank-string?]]))

(def login-schema
  [:map
   [:email email?]
   [:password non-blank-string?]])

(def register-schema
  [:and
   [:map
    [:email email?]
    [:password non-blank-string?]
    [:confirm-password non-blank-string?]]
   [:fn {:error/message "passwords must match"
         :error/path    [:confirm-password]}
    (fn [{:keys [password confirm-password]}]
      (= password confirm-password))]])

(def initial-state
  {:auth {:account nil
          :validation-errors {:login nil
                              :register nil}}})

(rf/reg-event-fx
 ::login
 (fn [{:keys [db]} [_ data]]
   (let [errors (m/explain login-schema data)]
     (if errors
       {:db (assoc-in db [:auth :validation-errors :login] (me/humanize errors))}
       {:db (assoc-in db [:auth :validation-errors :login] nil)
        :fx [[:dispatch [::login-request data]]]}))))

(rf/reg-event-fx
 ::login-request
 (fn [_ [_ data]]
   {:fx [[:dispatch [:http {:url "/api/account/login"
                            :method :post
                            :data data
                            :on-success [::auth-success]
                            :on-failure [:http-failure]}]]]}))

(rf/reg-event-fx
 ::register
 (fn [{:keys [db]} [_ data]]
   (let [errors (m/explain register-schema data)]
     (if errors
       {:db (assoc-in db [:auth :validation-errors :register] (me/humanize errors))}
       {:db (assoc-in db [:auth :validation-errors :register] nil)
        :fx [[:dispatch [::register-request data]]]}))))

(rf/reg-event-fx
 ::register-request
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