(ns sleep.db.core
  (:require [re-frame.core :as rf]
            [reitit.frontend.controllers :as rfc]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [sleep.db.fx]
            [sleep.db.subs]
            [sleep.pages.auth.events :as auth.db]
            [sleep.pages.dashboard.events :as dashboard.db]))

(def base-url "http://localhost:8080")

(rf/reg-event-fx
 :initialize-db
 [(rf/inject-cofx :local-storage :token/refresh-token)]
 (fn [{:token/keys [refresh-token]} [_]]
   {:db (merge
         {:current-route nil
          :dark-mode false}
         auth.db/initial-state
         dashboard.db/initial-state)
    ;; :fx [(when refresh-token [:refresh-token])]
    }))

(rf/reg-event-fx
 :http
 (fn [_ [_ {:keys [method url data headers on-success on-failure]}]]
   {:http-xhrio {:method          (or method :get)
                 :uri             (str base-url url)
                 :params          data
                 :headers         headers
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :timeout         50000
                 :on-success      on-success
                 :on-failure      on-failure}}))

(rf/reg-event-db
 :navigated
 (fn [db [_ new-match]]
   (let [old-match (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     (assoc db :current-route
            (assoc new-match :controllers controllers)))))

(rf/reg-event-db
 :http-failure
 (fn [db [_ error]]
   (js/console.log error)
   db))

(rf/reg-event-fx
 :coercion-error
 (fn [{:keys [db]} [_ error]]
   (let [current-route (get-in db [:current-route])]
     (js/console.log error)
     (js/console.log :coercion-error current-route)
     {}
     #_{:fx [[:navigate! current-route-name  {:replace? true}]]})))

(rf/reg-event-db
 :toggle-dark-mode
 (fn [db _]
   {:db (update db :dark-mode not)}))

(rf/reg-event-fx
 :refresh-token
 [(rf/inject-cofx :local-storage :token/refresh-token)
  (rf/inject-cofx :local-storage :token/access-token)]
 (fn [{:token/keys [access-token
                    refresh-token]} _]
   {:fx [[:dispatch [:http {:method :post
                            :url "/api/auth/refresh"
                            :data {:access-token access-token
                                   :refresh-token refresh-token}
                            :on-success [:refresh-token-success]
                            :on-failure [:refresh-token-failure]}]]]}))

(rf/reg-event-fx
 :refresh-token-success
 (fn [_ [_ {:keys [data]}]]
   {:fx [[:set-local-storage [:token/access-token (:token/access-token data)]]
         [:set-local-storage [:token/refresh-token (:token/refresh-token data)]]]
    :dispatch-later [{:dispatch [:refresh-token] :ms (* 10 60 1000)}]}))

(rf/reg-event-fx
 :refresh-token-failure
 (fn [_ _]
   {:fx [[:set-local-storage [:token/access-token nil]]
         [:set-local-storage [:token/refresh-token nil]]]}))