(ns sleep.db
  (:require [re-frame.core :as rf]
            [sleep.components.dialogs :as dialogs]
            [sleep.components.auth :as auth]))

(def app-db
  {:dialog dialogs/initial-state
   :auth auth/initial-state})

(rf/reg-event-db
 :initialize-db
 (fn [] app-db))