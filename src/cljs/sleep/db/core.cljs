(ns sleep.db.core
  (:require [re-frame.core :as rf]
            [reitit.frontend.controllers :as rfc]
            sleep.db.fx
            sleep.db.subs))

(def app-db {})

(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   app-db))

(rf/reg-event-db
 :navigated
 (fn [db [_ new-match]]
   (let [old-match (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     (assoc db :current-route
            (assoc new-match :controllers controllers)))))