(ns sleep.utils.time
  (:require [tick.core :as t]))

(defn duration->instant [n interval]
  (t/>> (t/instant)
        (t/new-duration n interval)))