(ns ftlm.hearts.auth.auth
  (:require
   [buddy.auth.backends :as backends]
   [buddy.auth.middleware :as buddy]))


(defn ->user []
  ;; xdtdb -> user
  )

(def auth-middleware
  {:name ::auth
   :wrap #(buddy/wrap-authentication % (backends/session {:authfn iddentity}))})
