(ns sleep.db
  (:require [re-frame.core :as rf]))

(def app-db
  {:dialog {:open? false
            :type :login}})

(rf/reg-event-db
 :initialize-db
 (fn [] app-db))