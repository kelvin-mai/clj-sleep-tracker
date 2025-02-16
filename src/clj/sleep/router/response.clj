(ns sleep.router.response)

(defn wrap-response-schema [schema]
  [:map
   [:success :boolean]
   [:data schema]
   [:meta
    [:map
     [:request-method :keyword]
     [:headers :any]
     [:uri :string]
     [:parameters {:optional true} :any]
     [:identity {:optional true} :any]
     [:remote-addr :string]]]])

(def no-content-response
  (wrap-response-schema :nil))

(defn response
  ([status] (response status nil))
  ([status body]
   {:status status
    :body   {:success true
             :data body}}))

(def ok (partial response 200))
(def created (partial response 201))