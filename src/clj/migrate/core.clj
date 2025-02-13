(ns migrate.core
  (:require [taoensso.telemere :as t]
            [migratus.core :as migratus]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [sleep.system.core :refer [read-config]]))

(defn get-datasource [env]
  (let [ds (:postgres/db state/system)]
    (if ds
      ds
      (:postgres/db (ig/init (read-config env) #{:postgres/db})))))

(defn create-config [ds]
  {:migration-dir "migrations"
   :store         :database
   :db            {:datasource ds}})

(defn create-migration! [{:keys [name
                                 profile]}]
  (let [profile (or profile :dev)]
    (if (nil? name)
      (t/log! :error "please provide a name for the migration")
      (let [config (create-config (get-datasource profile))
            files  (migratus/create config name)]
        (t/log! :info ["created migration files" files])))))

(defn migrate! [{:keys [profile]}]
  (let [profile (or profile :dev)]
    (migratus/migrate (create-config (get-datasource profile)))
    (t/log! :info "migrations complete")))

(defn reset-db! [{:keys [profile]}]
  (let [profile (or profile :dev)]
    (migratus/reset (create-config (get-datasource profile)))
    (t/log! :info "database reset complete")))

(comment
  (create-config (get-datasource :dev))
  (create-migration! {:name "add-email-verification-columns"})
  (migrate! nil)
  (migratus/down (create-config (get-datasource :dev))
                 20250210221333))