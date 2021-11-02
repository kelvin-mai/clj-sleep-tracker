(ns sleep.utils.db
  (:require [honeysql.core :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn query-one!
  [db sql-map]
  (jdbc/execute-one! db
                     (sql/format sql-map)
                     {:return-keys true
                      :builder-fn rs/as-kebab-maps}))

(defn query!
  [db sql-map]
  (jdbc/execute! db
                 (sql/format sql-map)
                 {:return-keys true
                  :builder-fn rs/as-kebab-maps}))
