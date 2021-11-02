(ns sleep.routing.response)

(defn response [status body]
  {:status status
   :body {:success true
          :data body}})

(def ok (partial response 200))
(def created (partial response 201))
