(ns sleep.db.fx
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]))

(rf/reg-fx
 :navigate!
 (fn [k {:keys [params query replace?]}]
   (if replace?
     (rfe/replace-state k params query)
     (rfe/push-state k params query))))

(rf/reg-fx
 :set-local-storage
 (fn [[k v]]
   (.setItem (.-localStorage js/window) k v)))

(rf/reg-cofx
 :local-storage
 (fn [cofx k]
   (let [v (.getItem (.-localStorage js/window) k)]
     (assoc cofx k v))))