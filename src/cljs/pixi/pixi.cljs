(ns pixi-cljs.core)

(def stage (js/PIXI.Stage. 0x66ff99))

(def renderer (js/PIXI.autoDetectRenderer 400 300))

(.appendChild (.-body js/document) (.-view renderer))

(def texture (js/PIXI.Texture.fromImage "images/bunny.png"))

(def bunny (js/PIXI.Sprite. texture))

(set! (.-anchor.x bunny) 0.5)
(set! (.-anchor.y bunny) 0.5)

(set! (.-position.x bunny) 200)
(set! (.-position.y bunny) 150)

(. stage addChild bunny)

(defn animate[]
  (js/requestAnimFrame animate)
  (set! (.-rotation bunny) (+ 0.1 (.-rotation bunny)))
  (. renderer render stage))

(js/requestAnimFrame animate)
