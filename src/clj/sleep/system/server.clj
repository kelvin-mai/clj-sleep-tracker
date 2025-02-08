(ns sleep.system.server
  (:require [integrant.core :as ig]
            [org.httpkit.server :as http]))

(defmethod ig/init-key :http/server
  [_ {:keys [router config]}]
  (let [port (:http-port config)]
    (println "initializing http server on port" port)
    (http/run-server router {:port port})))

(defmethod ig/halt-key! :http/server
  [_ server]
  (println "stopping http server")
  (server :timeout 100))