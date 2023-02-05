(ns sleep.utils
  (:require ["dayjs" :as dayjs]))

(defn render-children [children]
  (into [:<>] children))

(defn dayjs-format
  ([date-str]
   (dayjs-format date-str "YYYY-MM-DD"))
  ([date-str format-str]
   (.format (dayjs date-str) format-str)))