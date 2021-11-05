(ns sleep.utils.schema
  (:require [malli.core :as m]
            [tick.core :as t]
            [clojure.string :as s]))

(def time?
  (m/-simple-schema
    {:type :time
     :pred t/time?}))

(def date?
  (m/-simple-schema
    {:type :date
     :pred t/date?}))

(def non-blank-string?
  (m/-simple-schema
    {:type :non-blank-string
     :pred #(and (string? %)
                 (not (s/blank? %)))}))
