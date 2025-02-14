(ns sleep.test-utils.fixtures
  (:require [integrant.core :as ig]
            [migrate.core :as migrate]
            [sleep.system.core :refer [read-config]]))

(def test-system (atom nil))

(defn with-system [f]
  (reset! test-system
          (ig/init (read-config :test)))
  (f)
  (ig/halt! @test-system)
  (reset! test-system nil))

(defn with-db [f]
  (migrate/migrate! {:profile :test})
  (f)
  (migrate/reset-db! {:profile :test}))