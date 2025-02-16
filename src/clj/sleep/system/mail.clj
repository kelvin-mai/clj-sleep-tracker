(ns sleep.system.mail
  (:require [taoensso.telemere :as t]
            [integrant.core :as ig]
            [sleep.mailer.core :refer [->SMTPMailer]]))

(defmethod ig/init-key :smtp/mailer
  [_ {:keys [config]}]
  (let [smtp-config (:smtp config)]
    (t/log! :info "initializing mailer")
    (->SMTPMailer smtp-config)))