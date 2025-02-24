(ns sleep.db.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :current-route
 (fn [db]
   (:current-route db)))

(rf/reg-sub
 :router
 (fn [db]
   (:router db)))

(rf/reg-sub
 :dark-mode
 (fn [db]
   (:dark-mode db)))