(ns sleep.mailer.core
  (:require [taoensso.telemere :as t]
            [postal.core :refer [send-message]]))

(defprotocol Mailer
  (send! [config message]))

(defrecord SMTPMailer [config]
  Mailer
  (send! [_ message]
    (t/log! {:level :info
             :data message}
            "sending email")
    (try (send-message config message)
         (catch Exception e
           (t/error! e "failed to send email")))))

(defrecord LogMailer [_]
  Mailer
  (send! [_ message]
    (t/log! {:level :info
             :data message}
            "simulating sending email")))