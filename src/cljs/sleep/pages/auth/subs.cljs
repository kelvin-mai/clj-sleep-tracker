(ns sleep.pages.auth.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::account
 (fn [db]
   (get-in db [:auth :account])))

(rf/reg-sub
 ::loading
 (fn [db]
   (get-in db [:auth :loading])))

(rf/reg-sub
 ::validation-errors
 (fn [db [_ type]]
   (get-in db [:auth :validation-errors type])))