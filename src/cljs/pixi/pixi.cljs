(ns pixi-cljs.core)

;; setup renderer
(def renderer (js/PIXI.autoDetectRenderer 400 300))

(.appendChild (.-body js/document) (.-view renderer))

;; setup stage
(def stage (js/PIXI.Stage. 0x66ff99))

(def bunny-texture (js/PIXI.Texture.fromImage "images/bunny.png"))

;; some helper functions for setting up sprites

;; I feel like set-position and set-anchor could be made easier
;; if I had a grasp of macros
(defn set-position [sprite x y]
  (set! (.-position.x sprite) x)
  (set! (.-position.y sprite) y))

(defn set-anchor [sprite x y]
  (set! (.-anchor.x sprite) x)
  (set! (.-anchor.y sprite) y))

(defn create-simple [texture]
  (def sprite (js/PIXI.Sprite. texture))
  (set-position sprite 200 100)
  (set-anchor sprite 0.5 0.5)
  (. stage addChild sprite)
  sprite)

;; function to remove all elements from the stage
(defn empty-stage [stage]
  (doall (map
   (fn [target]
     (.log js/console target)
     (. stage removeChild target)
     )
   ;; do a slice 0 here to copy the array as the stage array mutates with removes
   (.slice (.-children stage) 0))))

(empty-stage stage)

(create-simple bunny-texture)

;; setup bunny
(def bunny (js/PIXI.Sprite. bunny-texture))

(set! (.-anchor.x bunny) 0.5)
(set! (.-anchor.y bunny) 0.5)

(set! (.-position.x bunny) 200)
(set! (.-position.y bunny) 150)

(. stage addChild bunny)

(def my-bunny (create-simple bunny-texture))

;; update world function
(defn update-world[]
  (set! (.-rotation my-bunny) (+ 0.1 (.-rotation my-bunny)))
  (set! (.-position.x bunny) (+ 1 (.-position.x bunny)))
  )

;; setup animation loop
(defn animate[]
  (js/requestAnimFrame animate)
  (update-world)
  (. renderer render stage))

(js/requestAnimFrame animate)
