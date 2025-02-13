(ns sleep.utils.schema
  (:require [clojure.test.check.generators :as gen]
            [clojure.string :as s]
            [malli.core :as m]
            [tick.core :as t]
            [sleep.utils.generators :refer [gen-date
                                            gen-time
                                            gen-timestamp]]))

(def non-blank-string?
  (m/-simple-schema
   {:type                :non-blank-string
    :pred                #(and (string? %)
                               (not (s/blank? %)))
    :type-properties     {:error/message "must be a non-blank string"
                          :gen/gen       (gen/fmap s/join gen/string-alphanumeric)}
    :swagger/type        :non-blank-string
    :swagger/description "A non-blank string"}))

(def date?
  (m/-simple-schema
   {:type                :date
    :pred                t/date?
    :type-properties     {:error/message "must be a valid date"
                          :gen/gen       gen-date}
    :swagger/type        :date
    :swagger/description "A date"}))

(def time?
  (m/-simple-schema
   {:type                :time
    :pred                t/time?
    :type-properties     {:error/message "must be a valid time"
                          :gen/gen       gen-time}
    :swagger/type        :time
    :swagger/description "A time"}))

(def timestamp?
  (m/-simple-schema
   {:type                :timestamp
    :pred                inst?
    :type-properties     {:error/message "must be a valid timestamp"
                          :gen/gen       gen-timestamp}
    :swagger/type        :timestamp
    :swagger/description "A timestamp"}))

(def email?
  [:re {:error/message "must be a valid email address"}
   #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"])