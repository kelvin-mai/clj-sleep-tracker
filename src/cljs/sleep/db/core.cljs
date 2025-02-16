(ns sleep.db.core
  (:require [re-frame.core :as rf]))

(def app-db {})

(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   app-db))