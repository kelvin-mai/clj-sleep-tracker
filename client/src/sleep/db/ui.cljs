(ns sleep.db.ui
  (:require [re-frame.core :as rf]))

(def initial-state
  {::dialog {:open? false
             :type :loading
             :error-message ""}})

(rf/reg-event-db
 ::close-dialog
 (fn [db] (assoc-in db [::dialog :open?] false)))

(rf/reg-event-db
 ::set-dialog
 (fn [db [_ type error-message]]
   (assoc db ::dialog {:open? true
                       :type type
                       :error-message error-message})))

(rf/reg-sub
 ::dialog
 (fn [db]
   (get db ::dialog)))