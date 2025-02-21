(ns sleep.db.fx
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]))

(rf/reg-fx
 :navigate!
 (fn [k params query]
   (rfe/push-state k params query)))

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