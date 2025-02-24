(ns sleep.system.core
  (:require [taoensso.telemere :as t]
            [aero.core :as aero]
            [integrant.core :as ig]
            sleep.system.mail
            sleep.system.db
            sleep.system.router
            sleep.system.server))

(defmethod ig/init-key :system/config
  [_ config]
  (t/log! {:level :info
           :data config}
          "initializing system")
  config)

(defn read-config
  ([] (read-config :prod))
  ([profile]
   {:system/config (aero/read-config
                    "resources/config/config.edn"
                    {:profile profile})
    :postgres/db   {:config (ig/ref :system/config)}
    :smtp/mailer   {:config (ig/ref :system/config)}
    :reitit/router {:config (ig/ref :system/config)
                    :db     (ig/ref :postgres/db)
                    :mailer (ig/ref :smtp/mailer)}
    :http/server   {:config (ig/ref :system/config)
                    :router (ig/ref :reitit/router)}}))