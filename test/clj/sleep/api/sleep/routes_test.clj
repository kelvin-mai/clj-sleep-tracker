(ns sleep.api.sleep.routes-test
  (:require [clojure.test :refer :all]
            [malli.generator :as mg]
            [sleep.utils.query :as q]
            [sleep.test-utils.fixtures :refer [test-system
                                               with-db
                                               with-system]]
            [sleep.test-utils.router :refer [request]]
            [sleep.api.account.schema :as account.schema]
            [sleep.api.sleep.schema :as sleep.schema]))

(use-fixtures :once with-system with-db)

(defn- equivalent-time? [from-params from-req]
  (= (str from-params)
     (subs from-req 0 5)))

(deftest sleep-crud-routes-test
  (let [{db     :postgres/db
         router :reitit/router}     @test-system
        login-params                (mg/generate account.schema/login-body)
        response                    (request router {:request-method :post
                                                     :uri            "/api/account/register"
                                                     :body-params    (assoc login-params
                                                                            :confirm-password
                                                                            (:password login-params))})
        {token :token/access-token} (:data response)
        _                           (q/query! db {:update :account
                                                  :set    {:verified true}
                                                  :where  [:= :id (get-in response [:data :account/id])]})]
    (testing "create-sleep"
      (let [params  (mg/generate sleep.schema/create-body)
            created (request router {:request-method :post
                                     :uri            "/api/sleep"
                                     :headers        {"Authorization" (str "Bearer " token)}
                                     :body-params    params})]
        (is (= (str (:sleep-date params))
               (get-in created [:data :sleep/sleep-date])))
        (is (equivalent-time? (:start-time params)
                              (get-in created [:data :sleep/start-time])))
        (is (equivalent-time? (:end-time params)
                              (get-in created [:data :sleep/end-time])))

        (testing "get-sleep"
          (let [sleep (request router {:request-method :get
                                       :headers        {"Authorization" (str "Bearer " token)}
                                       :uri            (str "/api/sleep/" (get-in created [:data :sleep/sleep-date]))})]
            (is (= sleep created))))

        (testing "update-sleep"
          (let [params  (mg/generate sleep.schema/update-body)
                updated (request router {:request-method :put
                                         :uri            (str "/api/sleep/" (get-in created [:data :sleep/sleep-date]))
                                         :headers        {"Authorization" (str "Bearer " token)}
                                         :body-params    params})]
            (is (not (nil? (get-in updated [:data :sleep/updated-at]))))
            (is (= (get-in updated [:data :sleep/sleep-date])
                   (get-in created [:data :sleep/sleep-date])))
            (when (:sleep-date params)
              (is (equivalent-time? (:start-time params)
                                    (get-in updated [:data :sleep/start-time]))))
            (when (:sleep-date params)
              (is (equivalent-time? (:end-time params)
                                    (get-in updated [:data :sleep/end-time]))))))

        (testing "delete-sleep"
          (let [deleted (request router {:request-method :delete
                                         :uri            (str "/api/sleep/" (get-in created [:data :sleep/sleep-date]))
                                         :headers        {"Authorization" (str "Bearer " token)}})]
            (is (true? (:success deleted)))))))))