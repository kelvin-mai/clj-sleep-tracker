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
  (-> (read-config :dev) keys set)
  (halt)
  (restart)
  state/system
  (->> (router {:request-method :post
                :body-params    {:email    "me@kelvinmai.io"
                                 :password "password"}
                :uri            "/api/"})
       :body
       (muuntaja.core/decode "application/json"))
  (->> (router {:request-method :get
                :headers        {:authorization "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtZUBrZWx2aW5tYWkuaW8iLCJpYXQiOiIyMDI1LTAyLTA5VDA3OjM5OjQyLjIzNDM2ODY4N1oiLCJleHAiOiIyMDI1LTAyLTA5VDA3OjU0OjQyLjIzNDQwNTIyOVoifQ.UvBcnxPViyVgf27az8ZtgCSaTo2I9311zZgerGRmkXk"}
                :uri            "/api/accounts/"})
       :body
       (muuntaja.core/decode "application/json"))

  (->> (router {:request-method :post
                :body-params    {:email    "me@kelvinmai.io"
                                 :password "password"}
                :uri            "/api/accounts/login"})
       :body
       (muuntaja.core/decode "application/json"))
  ;
  )