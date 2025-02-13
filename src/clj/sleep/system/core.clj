(ns sleep.system.core
  (:require [taoensso.telemere :as t]
            [aero.core :as aero]
            [integrant.core :as ig]
            sleep.system.mail
            sleep.system.db
            sleep.system.router
            sleep.system.server))

(defmethod aero/reader 'ig/ref
  [_ _ v]
  (ig/ref v))

(defmethod ig/init-key :system/config
  [_ config]
  (t/log! :info ["Initializing system with config" config])
  config)

(defn read-config
  ([] (read-config :prod))
  ([profile]
   (aero/read-config
    "resources/config/config.edn"
    {:profile profile})))