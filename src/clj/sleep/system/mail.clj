(ns sleep.system.mail
  (:require [integrant.core :as ig]
            [sleep.mailer.core :refer [->SMTPMailer]]))

(defmethod ig/init-key :smtp/mailer
  [_ {:keys [config]}]
  (let [smtp-config (:smtp config)]
    (->SMTPMailer smtp-config)))