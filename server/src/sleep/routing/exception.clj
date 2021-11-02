(ns sleep.routing.exception
  (:require [reitit.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]))

(defn response
  ([status message request]
   (response status message request nil))
  ([status message request exception]
   {:status status
    :body (merge
           {:success false
            :message message
            :uri (:uri request)}
           (when exception
             exception))}))

(defn handle-exception
  [status message]
  (fn
    ([request]
     (response status message request))
    ([exception request]
     (response status message request
               {:exception (.getClass exception)
                :data (ex-data exception)}))))

(defn handle-coercion-exception
  [status message]
  (fn [exception request]
    (response status message request
              (coercion/encode-error (ex-data exception)))))

(def exception-middleware
  (exception/create-exception-middleware
    {::exception/default (handle-exception 500 "Internal server error")
     :muuntaja/decode (handle-exception 400 "Malformed request")
     ::coercion/request-coercion (handle-coercion-exception 400 "Malformed request")
     ::coercion/response-coercion (handle-coercion-exception 500 "Malformed response")
     java.sql.SQLException (handle-exception 500 "Database exception")
     ::exception/wrap (fn [handler e request]
                        (println e (:uri request))
                        (handler e request))}))
