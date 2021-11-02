(ns sleep.api.account.schema)

(def login-body
  [:map 
   [:username string?]
   [:password string?]])

(def register-body
  [:map 
   [:username string?]
   [:password string?]])
