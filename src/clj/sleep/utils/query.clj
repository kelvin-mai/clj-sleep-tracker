(ns sleep.utils.query
  (:require [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.date-time]
            [next.jdbc.result-set :as rs]))

(extend-protocol rs/ReadableColumn
  java.sql.Date
  (read-column-by-label [^java.sql.Date v _]     (.toLocalDate v))
  (read-column-by-index [^java.sql.Date v _ _] (.toLocalDate v))

  java.sql.Timestamp
  (read-column-by-label [^java.sql.Timestamp v _]     (.toInstant v))
  (read-column-by-index [^java.sql.Timestamp v _ _] (.toInstant v))

  java.sql.Time
  (read-column-by-label [^java.sql.Time v _]     (.toLocalTime v))
  (read-column-by-index [^java.sql.Time v _ _] (.toLocalTime v)))

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