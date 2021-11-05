(ns scratch
  (:require [reitit.coercion.malli]
            [reitit.coercion :as coercion]
            [reitit.core :as r]
            [malli.core :as m]
            [malli.util :as mu]
            [malli.registry :as mr]
            [malli.generator :as mg]
            [malli.transform :as mt]
            [tick.core :as t]
            [schema.core :as s]
            [clojure.test.check.generators :as gen]))

(def time?
  (m/-simple-schema
    {:type :time
     :pred t/time? }))

(def date?
  (m/-simple-schema
    {:type :date
     :pred t/date? }))

(defn time-transformer
  []
  (mt/transformer
   {:encoders
    {:time {:compile
            (fn [schema _]
              {:enter #(if (inst? %)
                         (-> %
                             (t/instant)
                             (t/time))
                         %)})}}
    :date {:compile
           (fn [schema _]
             {:enter #(if (inst? %)
                        (t/date %)
                        %)})}}
   {:decoders
    {:time {:compile
            (fn [schema _]
              {:enter #(when (string? %)
                         (try
                           (t/time %)
                           (catch Exception e (str "Not a valid time: " e))))})}
     :date {:compile
            (fn [schema _]
              {:enter #(when (string? %)
                         (try
                           (t/date %)
                           (catch Exception e (str "Not a valid date: " e))))})}}}))

(def json-transformer
  (mt/transformer
    mt/json-transformer
    time-transformer
    mt/default-value-transformer))

(def string-transformer
  (mt/transformer
    mt/string-transformer
    time-transformer
    mt/default-value-transformer))

(def mai-coercion
  (reitit.coercion.malli/create
    (-> reitit.coercion.malli/default-options
        (assoc-in [:transformers :body :formats "application/json"] json-transformer)
        (assoc-in [:transformers :string :default] string-transformer))))

(def router
  (r/router 
    ["/:temp" {:coercion mai-coercion
               :muuntaja muuntaja.core/instance
               :parameters {:path [:map
                                   [:temp date?]]}}]
    {:compile coercion/compile-request-coercers}
    ))

(defn match-by-path-and-coerce! [path]
  (if-let [match (r/match-by-path router path)]
    (assoc match :parameters (coercion/coerce! match))))

(comment 
  (java.util.Date.)

  (t/date)
(match-by-path-and-coerce! "/2021-11-04")
(t/time "10:00")

(t/time? #time/time "10:00" )
)
