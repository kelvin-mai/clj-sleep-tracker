(ns sleep.utils)

(defn render-children [children]
  (map-indexed
   (fn [child i]
     (with-meta child {:key i}))
   children))