(ns ftlm.hearts.client
  (:require
   [shadow.graft :as graft]
   [shadow.cljs.modern :refer (js-await)]
   [cljs.reader :as reader]))

;; grab csrf token from html head meta, could alternatively be set via a graft
;; (def csrf-token (.-content (js/document.querySelector "meta[name=x-csrf-token]")))

;; look ma, no libs. these should likely be library functions
;; should obviously do more validation and error checking here, but for our purposes this is enough
;; (defn req [href opts]
;;   (js-await [res (js/fetch href (clj->js (assoc-in opts [:headers "x-csrf-token"] csrf-token)))]
;;             (.text res)))

(defmethod graft/scion "just-log-data" [opts container]
  (js/console.log "just-log-data" opts container))

(defonce state (atom {:curr-clip nil}))

(defmethod graft/scion "clip" [{:clip/keys [timestamps]} btn]
  (.addEventListener
   btn "click"
   (fn [e]
     (js/console.log "hiiii" timestamps)
     (when-let [id (:curr-clip @state)]
       (js/window.clearInterval id))
     (swap! state assoc
      :curr-clip
      (js/setInterval
       (fn
         []
         (js/window.navigator.vibrate (clj->js timestamps)))
       1000)))))

(defn init [] (graft/init reader/read-string))
