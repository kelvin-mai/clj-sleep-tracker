(ns sleep.utils.schema
  (:require [malli.core :as m]
            [tick.core :as t]
            [clojure.string :as s]))

(def non-blank-string?
  (m/-simple-schema
   {:type            :non-blank-string
    :pred            #(and (string? %)
                           (not (s/blank? %)))
    :type-properties {:error/message "must be a non-blank string"}}))

(def date?
  (m/-simple-schema
   {:type            :date
    :pred            t/date?
    :type-properties {:error/message "must be a valid date"}}))

(def time?
  (m/-simple-schema
   {:type            :time
    :pred            t/time?
    :type-properties {:error/message "must be a valid time"}}))