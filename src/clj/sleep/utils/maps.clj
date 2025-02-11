(ns sleep.utils.maps)

(defn map->ns-map [ns m]
  (reduce-kv (fn [acc k v]
               (assoc acc
                      (keyword ns (name k))
                      v))
             {}
             m))