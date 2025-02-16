(ns sleep.utils.query
  (:require [clojure.string :as s]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.date-time]
            [next.jdbc.prepare :as p]
            [next.jdbc.result-set :as rs])
  (:import (java.sql PreparedStatement)))

(defn <-pgobject [v]
  (let [type (.getType v)
        value (.getValue v)]
    (case type
      "citext" (s/lower-case value)
      v)))

(extend-protocol rs/ReadableColumn
  java.sql.Date
  (read-column-by-label [^java.sql.Date v _]   (.toLocalDate v))
  (read-column-by-index [^java.sql.Date v _ _] (.toLocalDate v))

  java.sql.Timestamp
  (read-column-by-label [^java.sql.Timestamp v _]   (.toInstant v))
  (read-column-by-index [^java.sql.Timestamp v _ _] (.toInstant v))

  java.sql.Time
  (read-column-by-label [^java.sql.Time v _]   (.toLocalTime v))
  (read-column-by-index [^java.sql.Time v _ _] (.toLocalTime v))

  org.postgresql.util.PGobject
  (read-column-by-label [^org.postgresql.util.PGobject v _] (<-pgobject v))
  (read-column-by-index [^org.postgresql.util.PGobject v _ _] (<-pgobject v)))

(extend-protocol p/SettableParameter
  java.time.LocalDate
  (set-parameter [^java.time.LocalDate v ^PreparedStatement ps idx]
    (.setObject ps idx v))
  java.time.Instant
  (set-parameter [^java.time.Instant v ^PreparedStatement ps idx]
    (.setObject ps idx v))
  java.time.LocalTime
  (set-parameter [^java.time.LocalTime v ^PreparedStatement ps idx]
    (.setObject ps idx v)))

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