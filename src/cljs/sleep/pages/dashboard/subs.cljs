(ns sleep.pages.dashboard.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::loading
 (fn [db]
   (get-in db [:dashboard :loading])))