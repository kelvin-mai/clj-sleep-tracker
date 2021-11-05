(ns sleep.routing.coercion
  (:require [reitit.coercion.malli :as rm]
            [malli.transform :as mt]
            [tick.core :as t]))

(defn encode-time
  [schema _]
  {:enter #(if (inst? %)
             (-> %
                 (t/instant)
                 (t/time))
             %)})

(defn encode-date
  [schema _]
  {:enter #(if (inst? %)
             (t/date %)
             %)})

(defn decode-time
  [schema _]
  {:enter #(when (string? %)
             (try
               (t/time %)
               (catch Exception e
                 (str "Not a valid time: " e))))})

(defn decode-date
  [schema _]
  {:enter #(when (string? %)
             (try 
               (t/date %)
               (catch Exception e
                 (str "Not a valid date: " e))))})

(defn time-transformer []
  (mt/transformer
    {:encoders {:time {:compile encode-time}
                :date {:compile encode-date}}}
    {:decoders {:time {:compile decode-time}
                :date {:compile decode-date}}}))

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

(def coercion
  (rm/create
    (-> rm/default-options
        (assoc-in [:transformers :body :formats "application/json"] json-transformer)
        (assoc-in [:transformers :string :default] string-transformer))))