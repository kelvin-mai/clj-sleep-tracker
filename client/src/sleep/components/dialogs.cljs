(ns sleep.components.dialogs
  (:require [re-frame.core :as rf]
            [sleep.utils :refer [render-children]]
            ["@headlessui/react" :refer [Dialog Transition]]))

(def Panel (.-Panel Dialog))
(def Title (.-Title Dialog))
(def Description (.-Description Dialog))
(def TransitionChild (.-Child Transition))

(defn dialog-overlay []
  [:> TransitionChild {:enter "ease-out duration-300"
                       :enter-from "opacity-0"
                       :enter-to "opacity-100"
                       :leave "ease-in duration-200"
                       :leave-from "opacity-100"
                       :leave-to "opacity-0"}
   [:div {:class "fixed inset-0 bg-black bg-opacity-50"
          :on-click #(rf/dispatch [:toggle-dialog])
          :aria-hidden true}]])

(defn dialog-panel [& children]
  [:div {:class "fixed inset-0 overflow-y-auto"}
   [:div {:class "flex min-h-full items-center justify-center p-4 text-center"}
    [:> TransitionChild {:enter "ease-out duration-300"
                         :enter-from "opacity-0 scale-95"
                         :enter-to "opacity-100 sacel-100"
                         :leave "ease-in duration-200"
                         :leave-from "opacity-100 scale-100"
                         :leave-to "opacity-0 scale-95"}
     [:> Panel {:class "w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all"}
      (render-children children)]]]])

(defn login-dialog []
  [:<>
   [:> Title {:as "h3"
              :class "text-lg font-medium"}
    "Login dialog"]
   [:> Description]])

(defn entry-dialog []
  [:<>
   [:> Title {:as "h3"}
    "Entry dialog"]
   [:> Description]])

(defn dialog []
  (let [{:keys [open? type]} @(rf/subscribe [:dialog])]
    [:> Transition {:appear true
                    :show open?}
     [:> Dialog {:class "relative z-10"
                 :on-close #(rf/dispatch [:toggle-dialog])}
      [dialog-overlay]

      [dialog-panel
       (case type
         :login [login-dialog]
         :entry [entry-dialog])]]]))