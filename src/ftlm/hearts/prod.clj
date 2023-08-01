(ns ftlm.hearts.prod
  (:gen-class)
  (:require [ftlm.hearts.server]))

(defn -main [& args]
  (ftlm.hearts.server/start!))
