(ns sleep.api.account.db-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [malli.generator :as mg]
            [buddy.hashers :as hashers]
            [sleep.api.account.db :as account.db]
            [sleep.api.account.schema :as account.schema]
            [sleep.test-utils.fixtures :refer [test-system
                                               with-system
                                               with-db]]))

(use-fixtures :once with-system with-db)

(deftest account-db-test
  (let [{db :postgres/db} @test-system]
    (testing "create-account!"
      (let [params (mg/generate account.schema/login-body)
            created-account (account.db/create-account! db params)]
        (is (= (:account/email created-account)
               (s/lower-case (:email params))))
        (is (:valid (hashers/verify (:password params)
                                    (:account/password created-account))))

        (testing "get-account-by-email"
          (is (= (account.db/get-account-by-email db (:account/email created-account))
                 created-account)))

        (testing "get-account-by-id"
          (is (= (account.db/get-account-by-id db (:account/id created-account))
                 created-account)))

        (testing "regenerate-verification-code!"
          (let [updated-account (account.db/regenerate-verification-code! db
                                                                          (:account/email created-account))]
            (is (false? (:account/verified created-account)))
            (is (false? (:account/verified updated-account)))
            (is (not= (:account/verification-code updated-account)
                      (:account/verification-code created-account)))

            (testing "verify-account!"
              (let [verified-account (account.db/verify-account! db
                                                                 (:account/id updated-account)
                                                                 (:account/verification-code updated-account))]
                (is (:account/verified verified-account))))))))))