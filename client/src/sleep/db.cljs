(ns sleep.db
  (:require [re-frame.core :as rf]))

(def app-db
  {:counter 0})

(rf/reg-event-db
 :initialize-db
 (fn [] app-db))

(rf/reg-event-db
 :increase
 (fn [db]
   (update db :counter inc)))

(rf/reg-event-db
 :decrease
 (fn [db]
   (update db :counter dec)))
