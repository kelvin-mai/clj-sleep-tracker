(ns sleep.system.server
  (:require [taoensso.telemere :as t]
            [integrant.core :as ig]
            [org.httpkit.server :as http]))

(defmethod ig/init-key :http/server
  [_ {:keys [router config]}]
  (let [port (:http-port config)]
    (t/log! {:level :info
             :data port}
            "initializing http server on port")
    (http/run-server router {:port port})))

(defmethod ig/halt-key! :http/server
  [_ server]
  (t/log! :info "stopping http server")
  (server :timeout 100))