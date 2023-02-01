(ns sleep.subscriptions
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :dialog
 (fn [db] (:dialog db)))