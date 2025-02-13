(ns sleep.utils.generators
  (:require [clojure.test.check.generators :as gen]
            [tick.core :as t]))

(defn format-time-number [n]
  (str (if (< n 0) 0 nil)
       n))

(def gen-date
  (gen/fmap #(t/date (-> (t/date "1900-01-01")
                         (t/>> (t/new-period % :days))))
            (gen/large-integer* {:min 0
                                 :max (* 120 365)})))

(def gen-time
  (->> gen/nat
       (gen/such-that #(< % 24))
       (gen/fmap #(str (format-time-number %) ":00"))
       (gen/fmap #(t/time %))))

(def gen-timestamp
  (gen/fmap #(t/instant %)
            (gen/large-integer* {:min 1000000000000})))