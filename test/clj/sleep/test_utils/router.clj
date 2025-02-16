(ns sleep.test-utils.router
  (:require [muuntaja.core :as m]))

(defn request [router request-map]
  (->> (router request-map)
       :body
       (m/decode "application/json")))