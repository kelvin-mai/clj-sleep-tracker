(ns sleep.db.core
  (:require [re-frame.core :as rf]
            [reitit.frontend.controllers :as rfc]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [sleep.db.fx]
            [sleep.db.subs]))

(def base-url "http://localhost:8080")

(rf/reg-event-db
 :initialize-db
 (fn [_ [_]]
   {}))

(rf/reg-event-fx
 :http
 (fn [_ [_ {:keys [method url data headers on-success on-failure]}]]
   {:http-xhrio {:method          (or method :get)
                 :uri             (str base-url url)
                 :params          data
                 :headers         headers
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :timeout         50000
                 :on-success      on-success
                 :on-failure      on-failure}}))

(rf/reg-event-db
 :navigated
 (fn [db [_ new-match]]
   (let [old-match (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     (assoc db :current-route
            (assoc new-match :controllers controllers)))))

(rf/reg-event-db
 :http-failure
 (fn [db [_ error]]
   (js/console.log error)
   db))