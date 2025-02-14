(ns sleep.api.sleep.db-test
  (:require [clojure.test :refer :all]
            [malli.generator :as mg]
            [sleep.api.account.db :as account.db]
            [sleep.api.account.schema :as account.schema]
            [sleep.api.sleep.db :as sleep.db]
            [sleep.api.sleep.schema :as sleep.schema]
            [sleep.test-utils.fixtures :refer [test-system
                                               with-db
                                               with-system]]))

(use-fixtures :once with-system with-db)

(deftest sleep-db-test
  (let [{db :postgres/db} @test-system
        account (account.db/create-account! db
                                            (mg/generate account.schema/login-body))]
    (testing "create-sleep!"
      (let [params (assoc (mg/generate sleep.schema/create-body)
                          :account-id (:account/id account))
            created (sleep.db/create-sleep! db params)]
        (is (= (:sleep/account-id created)
               (:account/id account)))
        (is (= (:sleep/sleep-date created)
               (:sleep-date params)))
        (is (= (:sleep/start-time created)
               (:start-time params)))
        (is (= (:sleep/end-time created)
               (:end-time params)))

        (testing "get-sleep-by-account-id"
          (let [result (sleep.db/get-sleep-by-date db
                                                   {:date (:sleep/sleep-date created)
                                                    :account-id (:account/id account)})]
            (is (= result created))))

        (testing "update-sleep!"
          (let [params (dissoc (mg/generate sleep.schema/update-body) :sleep-date)
                updated (sleep.db/update-sleep! db
                                                {:date (:sleep/sleep-date created)
                                                 :account-id (:account/id account)}
                                                params)]
            (is (= (:sleep/account-id updated)
                   (:account/id account)))
            (is (= (:sleep/sleep-date updated)
                   (:sleep/sleep-date created)))
            (is (= (:sleep/start-time updated)
                   (:start-time params)))
            (is (= (:sleep/end-time updated)
                   (:end-time params)))))

        (testing "delete-sleep!"
          (sleep.db/delete-sleep! db
                                  {:date (:sleep/sleep-date created)
                                   :account-id (:account/id account)})
          (is (nil? (sleep.db/get-sleep-by-date db (:sleep/sleep-date created)))))))))