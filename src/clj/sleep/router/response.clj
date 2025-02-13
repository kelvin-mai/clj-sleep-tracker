(ns sleep.router.response)

(defn wrap-response-schema [schema]
  [:map
   [:success :boolean]
   [:data schema]])

(def no-content-response
  [:map
   [:success :boolean]])

(defn response
  ([status] (response status nil))
  ([status body]
   {:status status
    :body   (merge
             {:success true}
             (when body
               {:data body}))}))

(def ok (partial response 200))
(def created (partial response 201))