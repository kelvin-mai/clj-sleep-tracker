(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [integrant.repl :as ig-repl :refer [go halt]]
            [integrant.repl.state :as state]
            [nrepl.server]
            [cider.nrepl :refer [cider-nrepl-handler]]
            [sleep.system :refer [read-config]]))

(tools-ns/set-refresh-dirs "dev" "server/src")

(ig-repl/set-prep!
 (fn []
   (read-config :dev)))

(declare router db)

(defn start-interactive []
  (go)
  (def router (:reitit/routes state/system))
  (def db (:postgres/db state/system))
  :ready!)

(defn restart []
  (halt)
  (tools-ns/refresh :after 'user/start-interactive))

(comment
  (halt)
  (restart)
  state/system
  ;
  (-> router
      (reitit.ring/get-router)
      (reitit.core/match-by-path "/api/account"))
  (->> (router {:request-method :get
                :uri "/api/health-check"})
       :body
       (muuntaja.core/decode "application/json"))
  (->> (router {:request-method :post
                :uri "/api/account/login"
                :body-params {:username "user"
                              :password "password"}})
       :body
       (muuntaja.core/decode "application/json"))
  (->> (router {:request-method :get
                :uri "/api/sleep"
                :headers {:authorization (str "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50L2lkIjoiZDg0MzdlYzktOWM4MC00NDliLTliNjYtZjBmMzI5MjY1YzRmIiwiYWNjb3VudC91c2VybmFtZSI6InVzZXIifQ.tbV1Ol5CCBgVA_Y3r72W-28IUWwcbBfJ09px-pzxd4Q")}
                :body-params {:username "user"
                              :password "password"}})
       :body
       (muuntaja.core/decode "application/json"))
  ;
  )
