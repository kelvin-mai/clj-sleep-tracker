(ns sleep.components.dialogs
  (:require [re-frame.core :as rf]
            [sleep.utils :refer [render-children]]
            [sleep.components.auth :refer [auth-form logout-dialog]]
            ["@headlessui/react" :refer [Dialog Transition]]))

(def Panel (.-Panel Dialog))
(def Title (.-Title Dialog))
(def Description (.-Description Dialog))
(def TransitionChild (.-Child Transition))

(def initial-state
  {:open? false
   :type :loading
   :error-message ""})

(def dialog-types
  {:loading {:title "Loading"
             :description "Loading..."}
   :error {:title "Error"
           :description "Something went wrong. Please try again."}
   :auth {:title "Authentication"
          :description "Please login to view application."}
   :logout {:title "Log out?"
            :description "Do you want to leave the application?"}})

(rf/reg-event-db
 :close-dialog
 (fn [db] (assoc-in db [:dialog :open?] false)))

(rf/reg-event-db
 :set-dialog
 (fn [db [_ type error-message]]
   (assoc db :dialog {:open? true
                      :type type
                      :error-message error-message})))

(rf/reg-event-db
 :http-failure
 (fn [db [_ {:keys [response]}]]
   (assoc db :dialog {:open? true
                      :type :error
                      :error-message (:message response)})))

(rf/reg-sub
 :dialog
 (fn [db]
   (get db :dialog)))

(defn dialog-overlay []
  [:> TransitionChild {:enter "ease-out duration-300"
                       :enter-from "opacity-0"
                       :enter-to "opacity-100"
                       :leave "ease-in duration-200"
                       :leave-from "opacity-100"
                       :leave-to "opacity-0"}
   [:div {:class "fixed inset-0 bg-black bg-opacity-50"
          :aria-hidden true}]])

(defn dialog-panel [& children]
  (let [{:keys [type]} @(rf/subscribe [:dialog])
        {:keys [title description]} (get dialog-types type)]
    [:div {:class "fixed inset-0 overflow-y-auto"}
     [:div {:class "flex min-h-full items-center justify-center p-4 text-center"}
      [:> TransitionChild {:enter "ease-out duration-300"
                           :enter-from "opacity-0 scale-95"
                           :enter-to "opacity-100 sacel-100"
                           :leave "ease-in duration-200"
                           :leave-from "opacity-100 scale-100"
                           :leave-to "opacity-0 scale-95"}
       [:> Panel {:class "w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all"}
        [:> Title {:as "h3"
                   :class "text-lg font-medium"}
         title]
        [:> Description {:as "p"}
         description]
        children
        #_(render-children children)]]]]))

(defn error-dialog []
  (let [{:keys [error-message]} @(rf/subscribe [:dialog])]
    [:<>
     (when error-message
       [:p {:class "mt-4"} error-message])
     [:div {:class "flex justify-end mt-2"}
      [:button {:class "bg-indigo-500 text-white py-2 px-4 rounded-lg hover:bg-indigo-600"
                :on-click #(rf/dispatch [:close-dialog])}
       "Cancel"]]]))

(defn dialog []
  (let [{:keys [open? type]} @(rf/subscribe [:dialog])]
    [:> Transition {:appear true
                    :show open?}
     [:> Dialog {:class "relative z-10"
                 :on-close #(when (not= type :loading) (rf/dispatch [:close-dialog]))}
      [dialog-overlay]

      [dialog-panel
       (case type
         :auth [auth-form]
         :logout [logout-dialog]
         :error [error-dialog]
          ;;  :entry [entry-dialog]
         nil)]]]))