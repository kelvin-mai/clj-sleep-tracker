(ns sleep.router.exception
  (:require [reitit.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]))

(defn response
  ([status message request]
   (response status message request nil))
  ([status message request exception]
   {:status status
    :body   (merge
             {:success false
              :message message
              :uri     (:uri request)}
             (when exception
               exception))}))

(defn handle-exception [status message]
  (fn
    ([request]
     (response status message request))
    ([exception request]
     (response (or (:status (ex-data exception)) status)
               message request
               {:exception (.getClass exception)
                :data      (ex-data exception)}))))

(defn handle-coercion-exception
  [status message]
  (fn [exception request]
    (response status message request
              {:exception (.getClass exception)
               :data      (coercion/encode-error (ex-data exception))})))

(defn throw-exception
  ([message] (throw-exception message 500 ::default))
  ([message status] (throw-exception message status ::default))
  ([message status type]
   (let [n (namespace ::default)
         type (keyword n (name type))]
     (throw (ex-info message {:status status
                              :type type})))))

(def exception-middleware
  (exception/create-exception-middleware
   {::exception/default          (handle-exception 500 "Internal Server Error")
    :muuntaja/decode             (handle-exception 400 "Malformed request")
    ::coercion/request-coercion  (handle-coercion-exception 400 "Malformed request")
    ::coercion/response-coercion (handle-coercion-exception 500 "Malformed response")
    java.sql.SQLException        (handle-exception 500 "Database error")
    ::exception/wrap             (fn [handler e request]
                                   (println e (:uri request))
                                   (handler e request))}))

(def default-handlers
  {:not-found (handle-exception 404 "Not Found")
   :method-not-allowed (handle-exception 405 "Method Not Allowed")
   :not-acceptable (handle-exception 401 "Not acceptable")})