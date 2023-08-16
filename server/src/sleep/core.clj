(ns sleep.core
  (:require [sleep.system :refer [read-config]]
            [integrant.core :as ig]))

(defn -main []
  (println "application starting")
  (-> (read-config)
      (ig/init)))
