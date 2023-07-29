(ns ftlm.hearts.server
  (:require
   [integrant.core :as ig]

   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :as ring-defaults]
   [ring.util.response :as resp]

   [hiccup2.core :as h]


   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as rrc]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]))

(defn home-view [count]
  [:html
   [:bodyhiccup/hiccup {:mvn/version "2.0.0-RC1"}
    [:h1 "Welcome home!"]
    [:ul
     (for [i (range count)]
       [:li i])]]])

(defmethod ig/init-key :router/routes [_ _]
  [["/"
    {:get (constantly
           (-> (resp/response (str (h/html (home-view 10))))
               (resp/header "Content-Type" "text/html")))}]
   ["/api"
    ["/math" {:get {:parameters {:query {:x int?, :y int?}}
                    :responses {200 {:body {:total int?}}}
                    :handler (fn [{{{:keys [x y]} :query} :parameters}]
                               {:status 200
                                :body {:total (+ x y)}})}}]]])

(defmethod ig/init-key :handler/handler [_ {:keys [routes]}]
  (ring/ring-handler
   (ring/router
    routes
    {:data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [parameters/parameters-middleware
                         rrc/coerce-request-middleware
                         muuntaja/format-response-middleware]}})))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defonce system (atom nil))

(def config
  {:adapter/jetty {:port 8090
                   :handler (ig/ref :handler/handler)}
   :handler/handler {:routes (ig/ref :router/routes)}
   :router/routes {}})

(defn start! []
  (reset! system (ig/init config)))

(defn halt! []
  (when-let [system @system] (ig/halt! system)))

(comment
  (do (halt!)
      (start!))
  ;; http://localhost:8090

  )
