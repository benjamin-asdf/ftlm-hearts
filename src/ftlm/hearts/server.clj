(ns ftlm.hearts.server
  (:require
   [integrant.core :as ig]

   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :as ring-defaults]
   [ring.util.response :as resp]

   [hiccup2.core :as h]

   #_[muuntaja.core :as m]
   [muuntaja.middleware :as muuntaja]))

(defn home-view [count]
  [:html
   [:bodyhiccup/hiccup {:mvn/version "2.0.0-RC1"}
    [:h1 "Welcome home!"]
    [:ul
     (for [i (range count)]
       [:li i])]]])

(defn routes [])

#_(defn start-jetty! []
  (reset!
   server
   (jetty/run-jetty

    (-> #'handler
        muuntaja/wrap-format
        (ring-defaults/wrap-defaults ring-defaults/api-defaults))
    {:join? false
     :port 3428})))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (fn [_] (resp/response (str "Hello " name))))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))


(def system (atom nil))

(def config
  {:adapter/jetty {:port 8090
                   :handler (ig/ref :handler/greet)}
   :handler/greet {:name "Alice"}})

(defn start! []
  (reset! system (ig/init config)))

(defn halt! []
  (when-let [system @system] (ig/halt! system)))


(comment
  (start!)


  ;; http://localhost:8090

  )
