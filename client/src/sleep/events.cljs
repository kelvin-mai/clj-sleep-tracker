(ns sleep.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 :close-dialog
 (fn [db]
   (assoc-in db [:dialog :open?] false)))

(rf/reg-event-db
 :set-dialog
 (fn [db [_ type]]
   (assoc db :dialog {:open? true
                      :type type})))