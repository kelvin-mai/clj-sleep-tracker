(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [integrant.repl :as ig-repl :refer [go halt]]
            [integrant.repl.state :as state]
            [nrepl.server]
            [sleep.system.core :refer [read-config]]))

(ig-repl/set-prep! #(read-config :dev))

(declare router db)

(defn start-interactive []
  (go)
  (def router (:reitit/router state/system))
  (def db (:postgres/db state/system))
  :ready!)

(defn restart []
  (tools-ns/refresh :after 'user/start-interactive))

(comment
  (halt)
  (restart)
  state/system

  ;
  )