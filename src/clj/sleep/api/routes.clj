(ns sleep.api.routes)

(def health-route
  ["/"
   {:name ::health-check
    :get (fn [request]
    (println (:uri request))
           {:status 200
            :body {:success true}})}])

(def api-routes
  [["/api"
    health-route]])