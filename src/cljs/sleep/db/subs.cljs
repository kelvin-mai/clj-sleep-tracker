(ns sleep.db.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :current-route
 (fn [db]
   (:current-route db)))