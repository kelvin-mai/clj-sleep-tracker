(ns sleep.router.coercion
  (:require [reitit.coercion.malli :as rm]
            [malli.transform :as mt]
            [tick.core :as t]
            #?(:cljs [re-frame.core :as rf])))

(defn encode-time [_ _]
  {:enter #(if (inst? %)
             (-> % t/instant t/time)
             %)})

(defn decode-time [_ _]
  {:enter #(when (string? %)
             (try
               (t/time %)
               #?(:clj (catch Exception e
                         (str "Not a valid time: " e))
                  :cljs (catch js/Error e
                          (rf/dispatch [:coercion-error e])))))})

(defn encode-date [_ _]
  {:enter #(if (inst? %)
             (t/date %)
             %)})

(defn decode-date [_ _]
  {:enter #(when (string? %)
             (try
               (t/date %)
               #?(:clj (catch Exception e
                         (str "Not a valid date: " e))
                  :cljs (catch js/Error e
                          (rf/dispatch [:coercion-error e])))))})

(defn custom-transformer []
  (mt/transformer
   {:encoders {:time {:compile encode-time}
               :date {:compile encode-date}}}
   {:decoders {:time {:compile decode-time}
               :date {:compile decode-date}}}))

(def json-transformer
  (mt/transformer
   mt/json-transformer
   custom-transformer
   mt/default-value-transformer))

(def string-transformer
  (mt/transformer
   mt/string-transformer
   custom-transformer
   mt/default-value-transformer))

(def coercion
  (rm/create
   (-> rm/default-options
       (assoc-in [:transformers :body :formats "application/json"] json-transformer)
       (assoc-in [:transformers :string :default] string-transformer)
       #?(:cljs (assoc :validate false)))))