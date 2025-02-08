(ns sleep.core
  (:require [sleep.system.core :as system]
            [integrant.core :as ig]))

(defn -main []
  (-> (system/read-config)
      (ig/init)))