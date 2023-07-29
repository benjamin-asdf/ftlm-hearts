(ns ftlm.hearts.config
  (:require [integrant.core :as ig]))

(def config
  {:adapter/jetty {:port 8090, :handler (ig/ref :handler/greet)}
   :handler/greet {:name "Alice"}})

;; (def config
;;   (ig/read-string (slurp "config.edn")))
