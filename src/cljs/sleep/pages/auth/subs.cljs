(ns sleep.pages.auth.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::account
 (fn [db]
   (get-in db [::auth :account])))