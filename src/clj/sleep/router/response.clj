(ns sleep.router.response)

(defn response [status body]
  {:status status
   :body   body})

(def ok (partial response 200))
(def created (partial response 201))