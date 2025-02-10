(ns sleep.utils.time
  (:require [tick.core :as t]))

(defn is-expired? [s]
  (t/< (t/instant)
       (t/instant (t/new-duration s :seconds))))

(defn duration->instant [n interval]
  (t/>> (t/instant)
        (t/new-duration n interval)))