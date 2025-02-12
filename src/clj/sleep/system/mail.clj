(ns sleep.system.mail
  (:require [integrant.core :as ig]
            [sleep.mailer.core :refer [->SMTPMailer]]))

(defmethod ig/init-key :mail/sender
  [_ {:keys [config]}]
  (let [smtp-config (:smtp config)]
    (->SMTPMailer smtp-config)))