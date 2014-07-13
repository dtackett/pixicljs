(ns pixi-cljs.pixi-game
  (:require [pixi-cljs.renderer :as renderer]))

(defn build-pixi-game
  "done?"
  [id view world]
  { :id id
    :view view
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
(declare run-game)
(declare find-game)

(defn apply-map
  [f map]
  (reduce
   #(conj {(first %2) (f (second  %2))} %1)
   {}
   map
   ))

(defn run-games
  [games]
  (apply-map run-game games))

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

(def test-games (atom {}))

(defn add-game!
  [games game]
  (swap! games #(assoc % (:id game) game)))

(def simple-add-game!
  (partial add-game! test-games))

(def simple-world (build-pixi-world {1 (build-pixi-sprite {
                                                        :id 1
                                                        :texture "images/bunny.png"
                                                        :position {:x 50 :y 50}
                                                        :rotation 0
                                                        :anchor {:x 0.5 :y 0.5}
                                                        :update-fn
                                                          (fn
                                                            [e]
                                                            (assoc
                                                              e
                                                              :rotation
                                                              (-
                                                               (:rotation e)
                                                               0.1)))})}))

(def simple-world-b (build-pixi-world {1 (build-pixi-sprite {
                                                        :id 1
                                                        :texture "images/bunny.png"
                                                        :position {:x 50 :y 50}
                                                        :rotation 0
                                                        :anchor {:x 0.5 :y 0.5}
                                                        :update-fn
                                                          (fn
                                                            [e]
                                                            (assoc
                                                              e
                                                              :position
                                                              { :x 50
                                                               :y (+ 50 (* 30 (.sin js/Math (/ (.now js/Date) 200))))}))})}))

(simple-add-game! (build-pixi-game
                  :my-game
                  (build-pixi-view (.-body js/document) 100 100 0xaa6699)
                  simple-world))

(simple-add-game! (build-pixi-game
                  :my-other-game
                  (build-pixi-view (.-body js/document) 100 100 0xaa66ee)
                   simple-world-b))


(defn clone-game!
  [clone-id new-id]
  (simple-add-game!
   (build-pixi-game
    new-id
    (build-pixi-view
     (.-body js/document)
     100 100
     0x55aaee)
    (:world (clone-id @test-games)))))


(clone-game! :my-game :my-game-clone)

;; setup animation loop
(defn animate[]
  (do
    (swap! test-games run-games)
    (js/requestAnimFrame animate)))

(animate)
