(ns pixi-cljs.pixi-game
  (:require [pixi-cljs.renderer :as renderer]))

(defn build-pixi-game
  "done?"
  [view world]
  {:view view
    :world world})

(defn build-pixi-view
  "done?"
  [parent-element width height background-color]
  {:parent-element parent-element
   :renderer (js/PIXI.autoDetectRenderer width height)
   :stage (js/PIXI.Stage. background-color)})

(defn build-pixi-world
  "done?"
  [entities]
  {:entities entities})

(defn build-pixi-sprite
  "done?"
  [props]
  props)

(declare step-game)
(declare render-game)

(defn run-game
  "done?"
  [game]
  (-> game
    (step-game)
    (render-game)
    ))

(defn entity-update
  "done?"
  [e]
  (if (contains? e :update-fn)
    ((e :update-fn) e)
    e))

(defn step-game
  "done?"
  [game]
  (assoc game :world
    (assoc
      (:world game)
      :entities
      (reduce
       #(assoc %1 (:id %2) (entity-update %2))
       {}
       (vals (:entities (:world game)))))))

(defn render-game
  "wip"
  [game]
  (renderer/render-game game))

(def my-bunny (js/PIXI.Texture.fromImage "images/bunny.png"))

(def test-game (atom (build-pixi-game
                 (build-pixi-view (.-body js/document) 400 300 0xaa6699)
                 (build-pixi-world {1 (build-pixi-sprite {
                                                        :id 1
                                                        :texture my-bunny
                                                        :position {:x 10 :y 10}
                                                        :rotation 0
                                                        :anchor {:x 0.5 :y 0.5}
                                                        :update-fn
                                                        (fn
                                                          [e]
                                                          (assoc
                                                            e
                                                            :rotation
                                                            (+
                                                             0.1
                                                             (:rotation e))))})}))))


;; setup animation loop
(defn animate[]
  (do
    (swap! test-game run-game)
    (js/requestAnimFrame animate)))

(animate)
