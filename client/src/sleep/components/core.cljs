(ns sleep.components.core
  (:require [re-frame.core :as rf]
            [sleep.components.dialogs :refer [dialog]]
            [sleep.components.stats :refer [stats]]
            ["@heroicons/react/24/solid" :refer [PlusIcon
                                                 UserCircleIcon]]))

(defn navbar []
  (let [account @(rf/subscribe [:account])]
    [:nav {:class "flex justify-between p-4 border-b shadow bg-white items-center"}
     [:div {:class "text-lg text-bold"} "Sleep Tracker"]
     [:button {:on-click #(rf/dispatch [:set-dialog (if account
                                                      :logout
                                                      :auth)])}
      [:> UserCircleIcon {:class (str "inline-block w-8 h-8 " (when account "fill-indigo-500"))}]]]))

(defn header []
  [:<>
   [:h1 {:class "text-4xl text-center mb-4"}
    "Daily Sleep Tracker"]
   [:button {:class "rounded-lg bg-indigo-500 text-white px-4 py-2 hover:bg-indigo-600 mx-auto flex items-center"
             :on-click #(rf/dispatch [:set-dialog :entry])}
    [:> PlusIcon {:class "fill-white inline-block w-6 h-6"}]
    " New Entry"]])

(defn app []
  (let [account @(rf/subscribe [:account])]
    [:div {:class "bg-slate-300"}
     [navbar]
     [:section {:class "bg-white mx-auto pt-4 h-screen md:mt-4 md:border md:w-5/6 md:rounded-md md:shadow"}
      [header]
      (when account
        [stats])]
     [dialog]]))
