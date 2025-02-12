(ns sleep.mailer.core
  (:require [postal.core :refer [send-message]]))

(defprotocol Mailer
  (send! [config message]))

(defrecord SMTPMailer [config]
  Mailer
  (send! [_ message]
    (send-message config message)))