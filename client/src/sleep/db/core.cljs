(ns sleep.db.core
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [sleep.db.ui :as ui]
            [sleep.db.auth :as auth]
            [sleep.db.sleep :as sleep]))

(def base-url "http://localhost:8080")

(def app-db
  (merge {}
         ui/initial-state
         auth/initial-state
         sleep/initial-state))

(rf/reg-event-db
 :initialize-db
 (fn [] app-db))

(rf/reg-event-fx
 :http
 (fn [_ [_ {:keys [method url data headers on-success on-failure]}]]
   {:http-xhrio {:method (or method :get)
                 :uri (str base-url url)
                 :params data
                 :headers headers
                 :timeout 5000
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success on-success
                 :on-failure on-failure}}))

(rf/reg-event-db
 :http-failure
 (fn [db [_ {:keys [response]}]]
   (assoc db ::ui/dialog {:open? true
                          :type :error
                          :error-message (:message response)})))

(rf/reg-fx
 :set-local-storage
 (fn [[k v]]
   (.setItem (.-localStorage js/window) k v)))

(rf/reg-cofx
 :local-storage
 (fn [cofx k]
   (let [v (.getItem (.-localStorage js/window) k)]
     (assoc cofx k v))))