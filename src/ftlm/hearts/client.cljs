(ns ftlm.hearts.client
  (:require
   [shadow.graft :as graft]
   [shadow.cljs.modern :refer (js-await)]
   [cljs.reader :as reader]

   [thi.ng.math.core :as m :refer [PI HALF_PI TWO_PI]]
   [thi.ng.color.core :as col]
   [thi.ng.typedarrays.core :as arrays]
   [thi.ng.geom.gl.core :as gl]
   [thi.ng.geom.gl.webgl.constants :as glc]
   [thi.ng.geom.gl.webgl.animator :as anim]
   [thi.ng.geom.gl.buffers :as buf]
   [thi.ng.geom.gl.shaders :as sh]
   [thi.ng.geom.gl.utils :as glu]
   [thi.ng.geom.gl.glmesh :as glm]
   [thi.ng.geom.gl.camera :as cam]
   [thi.ng.geom.core :as g]
   [thi.ng.geom.vector :as v :refer [vec2 vec3]]
   [thi.ng.geom.matrix :as mat :refer [M44]]
   [thi.ng.geom.aabb :as a]
   [thi.ng.geom.attribs :as attr]
   [thi.ng.glsl.core :as glsl :include-macros true]

   [thi.ng.geom.viz.core :as viz]
   [thi.ng.geom.svg.core :as svg]

   [thi.ng.geom.gl.shaders.basic :as basic]
   [thi.ng.geom.circle :as c]
   [thi.ng.geom.polygon :as poly]
   [thi.ng.geom.rect :as rect]

   [thi.ng.geom.gl.shaders.lambert :as lambert]

   )

  ;; (:require-macros
  ;;  [thi.ng.math.macros :as mm])
  )

(def csrf-token (atom nil))

(defmethod graft/scion "csrf-token" [opts]
  (reset! csrf-token (:token opts)))

;; look ma, no libs. these should likely be library functions
;; should obviously do more validation and error checking here, but for our purposes this is enough
(defn req [href opts]
  (js-await [res (js/fetch href (clj->js (assoc-in opts [:headers "x-csrf-token"] @csrf-token)))]
    (.text res)))

(defonce state (atom {:curr-clip nil}))

(defn start-clip! [{:clip/keys [timestamps]}]
  (when-let [id (:curr-clip @state)]
    (js/window.clearInterval id))
  (swap! state assoc
         :curr-clip
         (js/setInterval
          (fn
            []
            (js/window.navigator.vibrate (clj->js timestamps)))
          1000)
         :animating? true))

(defmethod graft/scion "clip" [opts btn]
  (.addEventListener btn "click" (fn [_] (start-clip! opts))))

(defmethod graft/scion "close-button" [opts btn]
  (.addEventListener
   btn "click"
   (fn [_]
     (set! (.. (js/document.getElementById (-> opts :data :elm-id)) -style -display) "none"))))

(defn ^:export demo
  []
  (let [gl        (gl/gl-context "main")
        view-rect (gl/get-viewport-rect gl)
        shader1   (sh/make-shader-from-spec gl (basic/make-shader-spec-2d false))
        model
        (-> (poly/cog 0.5 6 [0.9 1 1 0.9])
            (gl/as-gl-buffer-spec {:normals false})
            (gl/make-buffers-in-spec gl glc/static-draw)
            (assoc-in [:uniforms :proj] (gl/ortho view-rect)))]
    (anim/animate
     (fn [t frame]
       (gl/set-viewport gl view-rect)
       (gl/clear-color-and-depth-buffer gl col/WHITE 1)
       (when
           (:animating? @state)
           (gl/draw-with-shader
            gl (-> model
                   (assoc :shader shader1)
                   (update-in [:attribs] dissoc :color)
                   (update-in [:uniforms] merge
                              {:model
                               (-> M44
                                   (g/translate (vec3 0 0 0))
                                   (g/rotate (* HALF_PI t))
                                   (g/scale (- 1.6 (/ (Math/abs (Math/sin (* PI t))) 1.4))))
                               :color
                               [(+ 0.8 (Math/abs (Math/sin (* PI t))))
                                (Math/abs (Math/sin (* PI t)))
                                (+ 0.4 (Math/abs (Math/sin (* PI t))))
                                1]}))))
       true))))

(defn init []
  (graft/init reader/read-string)
  (demo))
