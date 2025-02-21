(ns sleep.pages.auth.views
  (:require [sleep.pages.auth.components :refer [page-container
                                                 login-form
                                                 register-form
                                                 forgot-password-form]]))

(defn login-page []
  [page-container
   [login-form]])

(defn register-page []
  [page-container
   [register-form]])

(defn forgot-password []
  [page-container
   [forgot-password-form]])

(def routes
  ["auth/"
   ["login" {:name ::login-route
             :view #'login-page
             :link-text "Login"
             :controllers []}]
   ["register" {:name ::register-route
                :view #'register-page
                :link-text "Register"
                :controllers []}]
   ["logout" {:name ::logout-route
              :view nil
              :link-text "Logout"
              :controllers []}]
   ["forgot-password" {:name ::forgot-password-route
                       :view forgot-password
                       :link-text "Forgot Password"
                       :controllers []}]])