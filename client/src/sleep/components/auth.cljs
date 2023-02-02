(ns sleep.components.auth
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))

(def initial-state
  {:account {}})

(rf/reg-event-fx
 :check-identity
 [(rf/inject-cofx :local-storage :account/token)]
 (fn [cofx]
   (let [token (:account/token cofx)]
     {:fx [[:dispatch [:http {:url "/api/account"
                              :method :get
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
   [:button {:class "bg-rose-500 text-white py-2 px-4 rounded-lg hover:bg-rose-600"
             :on-click #(rf/dispatch [:close-dialog])}
    "Cancel"]
   [:button {:class "bg-indigo-500 text-white py-2 px-4 rounded-lg hover:bg-indigo-600"
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
       [:div {:class "mb-4"}
        [:label {:class "block font-medium mb-2"
                 :for "username"} "Username"]
        [:input.w-full.border.border-gray-400.p-2.rounded-lg
         {:class "w-full border border-gray-400 p-2 rounded-md"
          :type "text"
          :id "username"
          :value (:username @form-state)
          :on-change (on-change :username)
          :required true}]]
       [:div {:class "mb-4"}
        [:label {:class "block font-medium mb-2"
                 :for "password"} "Password"]
        [:input {:class "w-full border border-gray-400 p-2 rounded-md"
                 :type "password"
                 :id "password"
                 :value (:password @form-state)
                 :on-change (on-change :password)
                 :required true}]]
       [:div {:class "flex justify-between"}
        [:button {:class "bg-sky-500 text-white py-2 px-4 rounded-lg hover:bg-sky-600"
                  :type "button"
                  :on-click #(rf/dispatch [:register @form-state])}
         "Register"]
        [:button {:class "bg-indigo-500 text-white py-2 px-4 rounded-lg hover:bg-indigo-600"
                  :type "submit"}
         "Login"]]])))