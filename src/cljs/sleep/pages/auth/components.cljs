(ns sleep.pages.auth.components
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [sleep.components.common :refer [button
                                             input
                                             separator]]
            [sleep.utils :refer [href]]
            [sleep.pages.auth.events :as auth.events]
            [sleep.pages.auth.subs :as auth.subs]))

(defn page-container [children]
  [:div {:class "grid min-h-svh lg:grid-cols-2"}
   [:div {:class "relative hidden bg-slate-700 lg:block"}
    [:img {:src   "/assets/gradient-bg.svg"
           :alt   "Background image"
           :class "absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"}]]
   [:div {:class "flex items-center justify-center"}
    children]])

(defn form-input [{:keys [id label type placeholder value on-change required? disabled?]}]
  [:div {:class "grid gap-2"}
   [:label {:class "text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
            :for   id}
    label]
   [input {:id          id
           :placeholder placeholder
           :type        type
           :value       value
           :on-change   on-change
           :required    required?
           :disabled    disabled?}]])

(defn auth-form-container [{:keys [type on-submit]}
                           children]
  (let [title       (if (= type :login)
                      "Login to your account"
                      "Sign up for an account")
        description "Please enter your email and password to log in."
        swap-text   (if (= type :login)
                      "Don't have an account? "
                      "Already have an account? ")
        swap-link   (if (= type :login)
                      {:link (href :sleep.pages.auth.views/register-route)
                       :text "Register now!"}
                      {:link (href :sleep.pages.auth.views/login-route)
                       :text "Log in."})
        submit-text (if (= type :login) "Login" "Register")]
    [:form {:class     "flex flex-col gap-6"
            :on-submit on-submit}
     [:div {:class "flex flex-col items-center gap-2 text-center"}
      [:h1 {:class "text-2xl font-bold"} title]
      [:p {:class "text-balance text-sm text-muted-foreground"} description]]
     children
     [button {:type "submit"} submit-text]
     [separator {:orientation "horizontal"}]
     [:div {:class "text-center text-sm"}
      [:div
       swap-text
       [:a {:class "underline-offset-4 hover:underline"
            :href  (:link swap-link)}
        (:text swap-link)]]
      [:a {:class "text-sm underline-offset-4 hover:underline"
           :href  (href :sleep.pages.auth.views/forgot-password-route)}
       "Forgot your password?"]]]))

(defn login-form []
  (let [form-state (r/atom {:email    ""
                            :password ""})
        on-change  (fn [k] #(swap! form-state assoc k (-> % .-target .-value)))]
    (fn []
      [auth-form-container {:type      :login
                            :on-submit (fn [e]
                                         (.preventDefault e)
                                         (rf/dispatch [::auth.events/login @form-state]))}
       [:<>
        [form-input {:id          :email
                     :type        "email"
                     :label       "Email Address"
                     :placeholder "your-name@email.com"
                     :value       (:email @form-state)
                     :on-change   (on-change :email)
                     :required?   true}]
        [form-input {:id          :password
                     :type        "password"
                     :label       "Password"
                     :placeholder "password"
                     :value       (:password @form-state)
                     :on-change   (on-change :password)
                     :required?   true}]]])))

(defn register-form []
  (let [form-state (r/atom {:email            ""
                            :password         ""
                            :confirm-password ""})
        on-change  (fn [k] #(swap! form-state assoc k (-> % .-target .-value)))]
    (fn []
      [auth-form-container {:type      :register
                            :on-submit (fn [e]
                                         (.preventDefault e)
                                         (rf/dispatch [::auth.events/register @form-state]))}
       [:<>
        [form-input {:id          :email
                     :type        "email"
                     :label       "Email Address"
                     :placeholder "your-name@email.com"
                     :value       (:email @form-state)
                     :on-change   (on-change :email)
                     :required?   true}]
        [form-input {:id          :password
                     :type        "password"
                     :label       "Password"
                     :placeholder "password"
                     :value       (:password @form-state)
                     :on-change   (on-change :password)
                     :required?   true}]
        [form-input {:id          :confirm-password
                     :type        "password"
                     :label       "Confirm Password"
                     :placeholder "confirm password"
                     :value       (:confirm-password @form-state)
                     :on-change   (on-change :confirm-password)
                     :required?   true}]]])))

(defn forgot-password-form []
  (let [email (r/atom "")]
    (fn []
      [:form {:class     "flex flex-col gap-6"
              :on-submit (fn [e]
                           (.preventDefault e)
                           (rf/dispatch [::auth.events/reset-password @email]))}
       [:div {:class "flex flex-col items-center gap-2 text-center"}
        [:h1 {:class "text-2xl font-bold"} "Forgot password"]]
       [form-input {:id          :email
                    :type        "email"
                    :label       "Email Address"
                    :placeholder "your-name@email.com"
                    :value       @email
                    :on-change   #(swap! email (-> % .-target .-value))
                    :required?   true}]
       [button {:type "submit"} "Reset Password"]])))