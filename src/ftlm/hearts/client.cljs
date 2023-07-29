(ns ftlm.hearts.client
  (:require
   [shadow.graft :as graft]
   [shadow.cljs.modern :refer (js-await)]
   [cljs.reader :as reader]))

;; grab csrf token from html head meta, could alternatively be set via a graft
(def csrf-token (.-content (js/document.querySelector "meta[name=x-csrf-token]")))

;; look ma, no libs. these should likely be library functions
;; should obviously do more validation and error checking here, but for our purposes this is enough
(defn req [href opts]
  (js-await [res (js/fetch href (clj->js (assoc-in opts [:headers "x-csrf-token"] csrf-token)))]
    (.text res)))

(defn append-html [container html]
  (let [temp (js/document.createElement "template")]
    (set! temp -innerHTML html)
    (.appendChild container (.-content temp))
    ))

(defn init []
  (graft/init reader/read-string js/document.body))
