(ns sleep.components.dialogs
  (:require [re-frame.core :as rf]
            ["@headlessui/react" :refer [Dialog Transition]]
            [sleep.utils :refer [render-children]]
            [sleep.components.auth :refer [auth-form logout-dialog]]
            [sleep.components.common :refer [button-class]]
            [sleep.components.entry :refer [entry-form confirm-entry confirm-delete]]
            [sleep.db.ui :as ui]))

(def Panel (.-Panel Dialog))
(def Title (.-Title Dialog))
(def Description (.-Description Dialog))
(def TransitionChild (.-Child Transition))

(def dialog-types
  {:loading {:title "Loading"
             :description "..."}
   :error {:title "Error"
           :description "Something went wrong. Please try again."}
   :auth {:title "Authentication"
          :description "Please login to view application."}
   :entry {:title "Sleep Entry"
           :description "Create or edit sleep at a given date."}
   :logout {:title "Log out?"
            :description "Do you want to leave the application?"}
   :confirm-entry {:title "Confirm submission"
                   :description "Sleep entry submission successful."}
   :confirm-delete {:title "Confirm deletion"
                    :description "Sleep entry successfully deleted."}})

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
  (let [{:keys [type]} @(rf/subscribe [::ui/dialog])
        {:keys [title description]} (get dialog-types type)]
    [:div {:class "fixed inset-0 overflow-y-auto"}
     [:div {:class "flex min-h-full items-center justify-center p-4 text-center"}
      [:> TransitionChild {:enter "ease-out duration-300"
                           :enter-from "opacity-0 scale-95"
                           :enter-to "opacity-100 sacel-100"
                           :leave "ease-in duration-200"
                           :leave-from "opacity-100 scale-100"
                           :leave-to "opacity-0 scale-95"}
       [:> Panel {:class "w-full max-w-md transform overflow-hidden rounded-md bg-white p-6 text-left align-middle shadow-xl transition-all"}
        [:> Title {:as "h3"
                   :class "text-lg font-medium"}
         title]
        [:> Description {:as "p"}
         description]
        (render-children children)]]]]))

(defn error-dialog []
  (let [{:keys [error-message]} @(rf/subscribe [::ui/dialog])]
    [:<>
     (when error-message
       [:p {:class "mt-4"} error-message])
     [:div {:class "flex justify-end mt-2"}
      [:button {:class (str button-class " bg-indigo-500 text-white hover:bg-indigo-600")
                :on-click #(rf/dispatch [::ui/close-dialog])}
       "Confirm"]]]))

(defn dialog []
  (let [{:keys [open? type]} @(rf/subscribe [::ui/dialog])]
    [:> Transition {:appear true
                    :show open?}
     [:> Dialog {:class "relative z-10"
                 :on-close #(when (not= type :loading) (rf/dispatch [::ui/close-dialog]))}
      [dialog-overlay]

      [dialog-panel
       (case type
         :auth [auth-form]
         :logout [logout-dialog]
         :error [error-dialog]
         :entry [entry-form]
         :confirm-entry [confirm-entry]
         :confirm-delete [confirm-delete]
         nil)]]]))