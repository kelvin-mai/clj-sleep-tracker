(ns sleep.db
  (:require [integrant.core :as ig]
            [hikari-cp.core :as hikari]))

(defmethod ig/init-key :postgres/db
  [_ {:keys [config]}]
  (let [options (:db config)]
    (println "starting database connection pool")
    (hikari/make-datasource options)))

(defmethod ig/halt-key! :postgres/db
  [_ ds]
  (println "closing database connection pool")
  (hikari/close-datasource ds))
