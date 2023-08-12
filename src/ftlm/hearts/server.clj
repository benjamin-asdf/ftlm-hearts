(ns ftlm.hearts.server
  (:require
   [integrant.core :as ig]

   [ring.adapter.jetty :as jetty]

   ;; [ring.util.response :as resp]
   [ftlm.hearts.html :refer [page-resp]]
   ;; [clojure.java.io :as io]
   ;; [xtdb.api :as xt]

   [shadow.graft :as graft]
   [shadow.css :refer [css]]

   [muuntaja.core :as m]
   [ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.defaults :refer [api-defaults] :as ring-defaults]
   [ring.middleware.session.memory :as memory]

   [reitit.ring :as ring]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as rrc]

   [reitit.ring.middleware.defaults :refer [ring-defaults-middleware]]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ftlm.hearts.auth.auth :as auth]
   [ftlm.hearts.auth.ui :as auth-ui]))

(def session-store (memory/memory-store))

;; lub, wait, dub, wait, diastole, repeat
(def clip {:clip/timestamps [250, 50, 100, 600]})

(def graft (graft/start pr-str))

(defn clip-page [req]
  (def req req)
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
   ["/login" {:get auth-ui/login
              :post auth/login}]
   ["/api"
    {:defaults api-defaults
     :middleware [auth/auth-middleware]}
    ["/clip/:clip-id"
     {:get {:handler #'clip-page}
      :delete {:handler (constantly nil)}
      :post {:handler (constantly nil)}}]]
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
   {:middleware
    [{:wrap wrap-gzip}
     ring-defaults-middleware]
    :defaults
    (-> ring-defaults/site-defaults
        (assoc-in [:session :store] session-store))}))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

;; login
;; logout
;; clip create

;; clip delete

;; users
;; login
;; auth,

(comment
  (reitit.core/match-by-path
   (ring/router (:router/routes @ftlm.hearts.system/system))
   "/login")
  )
