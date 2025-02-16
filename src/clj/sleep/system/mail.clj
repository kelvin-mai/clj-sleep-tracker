(ns sleep.system.mail
  (:require [taoensso.telemere :as t]
            [integrant.core :as ig]
            [sleep.mailer.core :refer [->SMTPMailer ->LogMailer]]))

(defmethod ig/init-key :smtp/mailer
  [_ {:keys [config]}]
  (let [{enabled :enabled
         :as smtp-config} (:smtp config)]
    (t/log! {:level :info
             :data (:smtp-config config)} "initializing mailer")
    (if enabled
      (->SMTPMailer smtp-config)
      (->LogMailer smtp-config))))