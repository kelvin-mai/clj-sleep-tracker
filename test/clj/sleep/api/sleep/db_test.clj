(ns sleep.api.sleep.db-test
  (:require [clojure.test :refer :all]
            [malli.generator :as mg]
            [tick.core :as t]
            [sleep.utils.query :as q]
            [sleep.api.account.db :as account.db]
            [sleep.api.account.schema :as account.schema]
            [sleep.api.sleep.db :as sleep.db]
            [sleep.api.sleep.schema :as sleep.schema]
            [sleep.test-utils.fixtures :refer [test-system
                                               with-db
                                               with-system]]))

(use-fixtures :once with-system with-db)

(deftest sleep-db-crud-test
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
            (when (:start-time params)
              (is (= (:sleep/start-time updated)
                     (:start-time params))))
            (when (:end-time params)
              (is (= (:sleep/end-time updated)
                     (:end-time params))))))

        (testing "delete-sleep!"
          (sleep.db/delete-sleep! db
                                  {:date (:sleep/sleep-date created)
                                   :account-id (:account/id account)})
          (is (nil? (sleep.db/get-sleep-by-date db (:sleep/sleep-date created)))))))))

(deftest sleep-db-list-test
  (let [{db :postgres/db} @test-system
        account (account.db/create-account! db
                                            (mg/generate account.schema/login-body))
        bulk-params (->> (range 10)
                         (map #(-> (t/instant)
                                   (t/>> (t/new-duration % :days))
                                   (t/date)))
                         (map #(assoc (mg/generate sleep.schema/create-body)
                                      :account-id (:account/id account)
                                      :sleep-date %)))
        bulk-sleeps (q/query! db {:insert-into [:sleep]
                                  :values bulk-params})]
    (testing "get-sleeps - all"
      (let [sleeps (sleep.db/get-sleeps db (:account/id account) {})]
        (is (= (count sleeps) (count bulk-sleeps)))))

    (testing "get-sleeps - with end-date"
      (let [days-ago (t/date (t/>> (t/instant) (t/new-duration 3 :days)))
            sleeps (sleep.db/get-sleeps db (:account/id account) {:end-date days-ago})]
        (is (= (count sleeps)
               (- (count bulk-sleeps) 3)))))

    (testing "get-sleeps - with start-date"
      (let [sleeps (sleep.db/get-sleeps db (:account/id account) {:start-date (t/date)})]
        (is (= (count sleeps) 1))))

    (testing "get-sleeps - with both dates"
      (let [start-date (t/date (t/>> (t/instant) (t/new-duration 7 :days)))
            end-date (t/date)
            sleeps (sleep.db/get-sleeps db (:account/id account) {:start-date start-date
                                                                  :end-date end-date})]
        (is (= (count sleeps) 8))))))