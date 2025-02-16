(ns sleep.db.fx
  (:require [re-frame.core :as rf]
            [reitit.core :as r]
            [reitit.frontend.easy :as rfe]))

(def base-url "http://localhost:8080")

(rf/reg-event-fx
 :http
 (fn [_ [_ {:keys [method url data headers on-success on-failure]}]]
   {:http-xhrio {:method        (or method :get)
                 :uri           (str base-url url)
                 :params data
                 :headers headers
                 :timeout       50000
                 :on-success    [on-success]
                 :on-failure    [on-failure]}}))

(rf/reg-fx
 :set-local-storage
 (fn [[k v]]
   (.setItem (.-localStorage js/window) k v)))

(rf/reg-cofx
 :local-storage
 (fn [cofx k]
   (let [v (.getItem (.-localStorage js/window) k)]
     (assoc cofx k v))))

(rf/reg-fx
 :push-state
 (fn [route]
   (apply rfe/push-state route)))