(ns sleep.utils.schema
  (:require [malli.core :as m]
            [clojure.string :as s]))

(def non-blank-string?
  (m/-simple-schema
   {:type :non-blank-string
    :pred #(and (string? %)
                (not (s/blank? %)))}))