(ns migrate.core
  (:require [migratus.core :as migratus]
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

(defn create-migration [{:keys [name]}]
  (if (nil? name)
    (println "please provide a name for the migration")
    (let [config (create-config (get-datasource :dev))
          files  (migratus/create config name)]
      (println "created migration files" files))))

(defn migrate [_]
  (migratus/migrate (create-config (get-datasource :dev)))
  (println "migrations complete"))

(comment
  (create-config (get-datasource :dev))
  (create-migration {:name "add-email-verification-columns"})
  (migrate nil)
  (migratus/down (create-config (get-datasource :dev))
                 20250210221333))