(ns sleep.components.core
  (:require [re-frame.core :as rf]
            ["@heroicons/react/24/solid" :refer [PlusIcon
                                                 UserCircleIcon]]
            [sleep.components.dialogs :refer [dialog]]
            [sleep.components.stats :refer [stats]]
            [sleep.db.auth :as auth]
            [sleep.db.sleep :as sleep]
            [sleep.db.ui :as ui]))

(defn navbar []
  (let [account @(rf/subscribe [::auth/account])]
    [:nav {:class "flex justify-between p-4 border-b shadow bg-white items-center"}
     [:div {:class "text-lg text-bold"} "Sleep Tracker"]
     [:button {:on-click #(rf/dispatch [::ui/set-dialog (if account
                                                          :logout
                                                          :auth)])}
      [:> UserCircleIcon {:class (str "inline-block w-8 h-8 " (when account "fill-indigo-500"))}]]]))

(defn header []
  [:<>
   [:h1 {:class "text-4xl text-center mb-4"}
    "Daily Sleep Tracker"]])

(defn app []
  (let [account @(rf/subscribe [::auth/account])]
    [:div {:class "bg-slate-300"}
     [navbar]
     [:section {:class "bg-white mx-auto pt-4 min-h-screen md:mt-4 md:border md:w-5/6 md:rounded-md md:shadow"}
      [header]
      (when account
        [:<>
         [:button {:class "rounded-lg bg-indigo-500 text-white px-4 py-2 hover:bg-indigo-600 mx-auto flex items-center"
                   :on-click #(rf/dispatch [::sleep/open-entry-form])}
          [:> PlusIcon {:class "fill-white inline-block w-6 h-6"}]
          " New Entry"]
         [stats]])]
     [dialog]]))
