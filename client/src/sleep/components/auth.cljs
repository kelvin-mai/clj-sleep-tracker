(ns sleep.components.auth
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [sleep.components.common :refer [form-input
                                             button-class]]
            [sleep.db.ui :as ui]
            [sleep.db.auth :as auth]))

(defn logout-dialog []
  [:div {:class "mt-2 flex justify-between"}
   [:button {:class (str button-class " bg-white text-gray-700 hover:bg-gray-50")
             :on-click #(rf/dispatch [::ui/close-dialog])}
    "Cancel"]
   [:button {:class (str button-class " bg-indigo-500 text-white hover:bg-indigo-600 ml-2")
             :on-click #(rf/dispatch [::auth/logout])}
    "Confirm"]])

(defn auth-form []
  (let [form-state (r/atom {:username ""
                            :password ""})
        on-change (fn [k] #(swap! form-state assoc k (-> % .-target .-value)))]
    (fn []
      [:form {:class "mt-4"
              :on-submit (fn [e]
                           (.preventDefault e)
                           (rf/dispatch [::auth/login @form-state]))}
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
                  :type "button"
                  :on-click #(rf/dispatch [::auth/register @form-state])}
         "Register"]
        [:button {:class (str button-class " bg-indigo-500 text-white hover:bg-indigo-600 ml-2")
                  :type "submit"}
         "Login"]]])))