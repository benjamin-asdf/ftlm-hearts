(ns ftlm.hearts.auth.ui
  (:require
   [ftlm.hearts.html :refer [page-resp]]
   [hiccup2.core :as h]
   [shadow.graft :as graft]
   [shadow.css :refer [css]]))

(defn login [_req]
  (page-resp
   [:form
    [:label {:for :username} "username:"]
    [:input {:type :text :plaholder "lol" :id :username}]
    [:label {:for :pw} "password:"]
    [:input {:type :text :plaholder "lol" :id :pw}]
    [:input {:type :submit :onClick "console.log('hi')"} "submit"]]))
