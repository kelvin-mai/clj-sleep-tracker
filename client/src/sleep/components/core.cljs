(ns sleep.components.core
  (:require [re-frame.core :as rf]
            [sleep.components.dialogs :refer [dialog]]
            ["@heroicons/react/24/solid" :refer [PlusIcon]]))

(defn navbar []
  [:nav {:class "flex justify-between p-4 border-b shadow bg-white"}
   [:div "Sleep Tracker"]
   [:button {:on-click #(rf/dispatch [:set-dialog :login])}
    "Login"]])

(defn header []
  [:<>
   [:h1 {:class "text-4xl text-center mb-4"}
    "Daily Sleep Tracker"]
   [:button {:class "rounded-lg bg-blue-500 text-white px-4 py-2 mx-auto flex items-center"
             :on-click #(rf/dispatch [:set-dialog :entry])}
    [:> PlusIcon {:class "fill-white inline-block w-6 h-6"}]
    " New Entry"]])

(defn app []
  [:div {:class "bg-slate-300"}
   [navbar]
   [:section {:class "bg-white mx-auto pt-4 h-screen md:mt-4 md:border md:w-5/6 md:rounded-md md:shadow"}
    [header]]
   [dialog]])