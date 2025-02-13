(ns sleep.system.db
  (:require [taoensso.telemere :as t]
            [integrant.core :as ig]
            [hikari-cp.core :as hikari]))

(defmethod ig/init-key :postgres/db
  [_ {:keys [config]}]
  (let [options (:db config)]
    (t/log! :info ["initializing database connection pool with options" options])
    (hikari/make-datasource options)))

(defmethod ig/halt-key! :postgres/db
  [_ ds]
  (t/log! :info "closing database connection pool")
  (hikari/close-datasource ds))