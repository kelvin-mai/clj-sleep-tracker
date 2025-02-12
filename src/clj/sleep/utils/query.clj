(ns sleep.utils.query
  (:require [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def jdbc-opts
  {:return-keys true
   :builder-fn  rs/as-kebab-maps})

(defn query-one!
  [db sql-map]
  (jdbc/execute-one! db
                     (sql/format sql-map)
                     jdbc-opts))

(defn query!
  [db sql-map]
  (jdbc/execute! db
                 (sql/format sql-map)
                 jdbc-opts))