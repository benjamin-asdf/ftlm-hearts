(ns ftlm.hearts.html
  (:require
   [hiccup2.core :as h]
   [ring.util.response :as resp]
   [shadow.css :refer [css]]
   [shadow.graft :as graft]
   [ring.middleware.anti-forgery :as csrf]))

(def graft (graft/start pr-str))

(defn anti-forgery-input-field []
  (when (bound? #'csrf/*anti-forgery-token*)
    [:input {:type "hidden" :name "__anti-forgery-token" :value csrf/*anti-forgery-token*}]))

(defn anti-forgery-graft []
  (when (bound? #'csrf/*anti-forgery-token*)
    (graft "csrf-token" :none {:token csrf/*anti-forgery-token*})))

(defn flash-message
  [{:keys [content type]}]
  (let [$color
        {:info (css :bg-sky-100)
         :warning (css :bg-yellow-200 :text-black)}]
    [:div
     {:id "flash-msg"
      :class (str
              (css
                :flex
                :justify-between
                :fixed :right-0 :top-0 :m-6 :p-2 :w-1of5 :shadow-sm)
              " " ($color (or type :info)))}
     [:div content]
     [:div [:button "X"]]
     (graft "close-button" :prev-sibling {:data {:elm-id "flash-msg"}})]))

(defn base [req body]
  (h/html
      {:escape-strings? false}
      [:head
       [:link {:rel "preload" :as "script" :href "/js/main.js"}]
       [:link {:rel "stylesheet" :href "/css/ui.css"}]
       [:title "ftl-hearts"]]
      [:body
       body
       (anti-forgery-graft)
       (when-let [msg (-> req :flash)]
         (flash-message msg))
       [:script {:type "text/javascript" :src "/js/main.js" :defer true}]]))

(defn page-resp [req body]
  (->
   (base req body)
   str
   resp/response
   (resp/header "Content-Type" "text/html")))
