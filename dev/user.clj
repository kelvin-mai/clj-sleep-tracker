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
  (->> (router {:request-method :get
                :uri "/api/account"
                :headers {"Authorization" "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50L2lkIjoxLCJhY2NvdW50L3VzZXJuYW1lIjoidXNlciJ9.uDi9rw4B2QgkCcHh5hJ-8Y6cbgPFKxQqCv1DN12fPaQ"}
                :body-params {:username "user"
                              :password "password"}})
       :body
       (muuntaja.core/decode "application/json"))
  (->> (router {:request-method :post
                :uri "/api/account/login"
                :body-params {:username "user"
                              :password "password"}})
       :body
       (muuntaja.core/decode "application/json"))
  (->> (router {:request-method :post
                :headers {"Authorization" "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50L2lkIjoiN2Y1YzBmYzAtNWFkNy00MDJkLWFmZmMtYjVlMTYxZjcwNmVjIiwiYWNjb3VudC91c2VybmFtZSI6InVzZXIifQ.-ciJ5lXMNx2GHoAaXAigykPnfrWyngPcWXptIFR_zSM"}
                :uri "/api/sleep"
                :body {:sleep-date (tick.core/date "2021-11-03")
                       :start-time (tick.core/time "10:00")
                       :end-time (tick.core/time "08:00")}
                })
       :body
       (muuntaja.core/decode "application/json"))
  
  (sleep.utils.db/query! db {:select [:*]
                             :from [:account]})
(sleep.utils.db/query! db
             {:select [:id
                       :sleep-date
                       :start-time
                       :end-time]
              :from [:sleep]})
  )
