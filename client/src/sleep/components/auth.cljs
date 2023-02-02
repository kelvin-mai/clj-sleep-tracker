(ns sleep.components.auth
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [sleep.components.common :refer [form-input
                                             button-class]]))

(def initial-state
  {:account {}})

(rf/reg-event-fx
 :check-identity
 [(rf/inject-cofx :local-storage :account/token)]
 (fn [cofx]
   (let [token (:account/token cofx)]
     {:fx [[:dispatch [:http {:url "/api/account"
                              :headers {"Authorization" (str "Bearer " token)}
                              :on-success [:auth-success]
                              :on-failure [:logout]}]]]})))

(rf/reg-event-fx
 :login
 (fn [_ [_ data]]
   {:fx [[:dispatch [:set-dialog :loading]]
         [:dispatch [:http {:url "/api/account/login"
                            :method :post
                            :data data
                            :on-success [:auth-success]
                            :on-failure [:http-failure]}]]]}))

(rf/reg-event-fx
 :register
 (fn [_ [_ data]]
   {:fx [[:dispatch [:http {:url "/api/register"
                            :method :post
                            :data data
                            :on-success [:auth-success]
                            :on-failure [:http-failure]}]]]}))
(rf/reg-event-fx
 :auth-success
 (fn [{:keys [db]} [_ {:keys [data]}]]
   {:db (assoc-in db [:auth :account] data)
    :fx [[:dispatch [:close-dialog]]
         [:set-local-storage [:account/token (:account/token data)]]]}))

(rf/reg-event-fx
 :logout
 (fn [{:keys [db]}]
   {:db (assoc-in db [:auth :account] nil)
    :fx [[:dispatch [:close-dialog]]
         [:set-local-storage [:account/token nil]]]}))

(rf/reg-sub
 :account
 (fn [db]
   (get-in db [:auth :account])))

(defn logout-dialog []
  [:div {:class "mt-2 flex justify-between"}
   [:button {:class (str button-class " bg-white text-gray-700 hover:bg-gray-50")
             :on-click #(rf/dispatch [:close-dialog])}
    "Cancel"]
   [:button {:class (str button-class " bg-indigo-500 text-white hover:bg-indigo-600 ml-2")
             :on-click #(rf/dispatch [:logout])}
    "Confirm"]])

(defn auth-form []
  (let [form-state (r/atom {:username ""
                            :password ""})
        on-change (fn [k] #(swap! form-state assoc k (-> % .-target .-value)))]
    (fn []
      [:form {:class "mt-4"
              :on-submit (fn [e]
                           (.preventDefault e)
                           (rf/dispatch [:login @form-state]))}
       [form-input {:id "username"
                    :label "Username"
                    :value (:username @form-state)
                    :on-change (on-change :username)
                    :required true}]
       [form-input {:id "password"
                    :label "Password"
                    :type "password"
                    :value (:password @form-state)
                    :on-change (on-change :password)}]
       [:div {:class "flex justify-between"}
        [:button {:class (str button-class " bg-teal-500 text-white hover:bg-teal-600")
                  :on-click #(rf/dispatch [:register @form-state])}
         "Register"]
        [:button {:class (str button-class " bg-indigo-500 text-white hover:bg-indigo-600 ml-2")
                  :type "submit"}
         "Login"]]])))