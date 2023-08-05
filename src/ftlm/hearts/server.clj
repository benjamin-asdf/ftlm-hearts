(ns ftlm.hearts.server
  (:require
   [integrant.core :as ig]

   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :as ring-defaults]
   [hiccup2.core :as h]
   [ring.util.response :as resp]

   [clojure.java.io :as io]
   [xtdb.api :as xt]

   [shadow.graft :as graft]
   [shadow.css :refer [css]]

   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as rrc]
   [ring.middleware.gzip :refer [wrap-gzip]]

   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]))

;; lub, wait, dub, wait, diastole, repeat
(def clip {:clip/timestamps [250, 50, 100, 600]})

(def graft (graft/start pr-str))

(defn base [body]
  (h/html
   {:escape-strings? false}
   [:head
    [:link {:rel "preload" :as "script" :href "/js/main.js"}]
    [:link {:rel "stylesheet" :href "/css/ui.css"}]
    [:title "ftl-hearts"]]

   [:body
    body
    [:script {:type "text/javascript" :src "/js/main.js" :defer true}]]))

(defn page-resp [body]
  (->
   (base body)
   str
   resp/response
   (resp/header "Content-Type" "text/html")))

(defn clip-page [req]
  (page-resp
   [:div.clip
    {:class (css :flex :justify-center)}
    [:div
     [:svg
      {:xmlns "http://www.w3.org/2000/svg" :width "200" :height "200" :viewBox "0 0 100 100"}
      [:circle {:cx "50" :cy "50" :r "50" :fill "orange"}]]
     (graft "clip" :prev-sibling clip)
     [:button {:class (css :px-4 :shadow {:background-color "red"})} "lub-dub"]
     (graft "clip" :prev-sibling clip)]]))

(defmethod ig/init-key :router/routes [_ _]
  [["/" {:get {:handler #'clip-page}}]
   ["/clip/:clip-id"
    {:get {:handler #'clip-page}}]])

(defmethod ig/init-key :handler/handler [_ {:keys [routes]}]
  (ring/ring-handler
   (ring/router
    routes
    {:data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [parameters/parameters-middleware
                         rrc/coerce-request-middleware
                         muuntaja/format-response-middleware]}})
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler))
   {:middleware [{:wrap wrap-gzip}]}))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :xtdb/node [_ opts]
  (xt/start-node opts))

(defmethod ig/halt-key! :xtdb/node [_ node]
  (.close node))

(defonce system (atom nil))

(defn xtdb-node [] (get @system :xtdb/node :not-initialized))

(def config
  {:adapter/jetty {:port 8093
                   :handler (ig/ref :handler/handler)}
   :handler/handler {:routes (ig/ref :router/routes)}
   :router/routes {}
   :xtdb/node
   (let [kv-store (fn [dir]
                    {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                :db-dir (io/file dir)
                                :sync? true}})]
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})})

(defn start! []
  (reset! system (ig/init config))
  (println "Started server on " (-> config :adapter/jetty :port)))

(defn halt! []
  (when-let [system @system] (ig/halt! system)))

(defn restart []
  (halt!)
  (start!))

(comment
  (restart)
  (xt/submit-tx ( xtdb-node) [[::xt/put {:xt/id "hi2u" :user/name "zig"}]])
  (xt/q (xt/db (xtdb-node)) '{:find [e] :where [[e :user/name "zig"]]})

  ;; http://localhost:8093
  ;; http://localhost:8093/clip/foo

  )


;; users
;; login
;; auth,
