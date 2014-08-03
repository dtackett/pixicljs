(ns pixi-cljs.pixi-game
  (:require [pixi-cljs.renderer :as renderer]))

(defn build-pixi-game
  "done?"
  [id view world]
  { :id id
    :view view
    :world world
    :origin world
    :running true})

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

(defn smooth-cycle
  [min max p]
  (+ min (* (- max min) (.abs js/Math (.sin js/Math (/ (.now js/Date) p))))))

(defn clamp-cycle
  [min max p]
  (.round js/Math (smooth-cycle min max p)))

(declare step-game)
(declare render-game)
(declare run-game)
(declare find-game)

(defn apply-map
  "Take all of the elements of a map, run the value through the given function and associate the result with the original key."
  [f map]
  (reduce
   #(conj {(first %2) (f (second  %2))} %1)
   {}
   map
   ))

(defn apply-merge
  "Take the value of the key in map, apply the function and associate the result under the original key."
  [map k func]
  (assoc map k (func (k map))))

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
  (cond (:running game)
        (assoc game :world
               (assoc
                   (:world game)
                 :entities
                 (reduce
                  #(assoc %1 (:id %2) (entity-update %2))
                  {}
                  (vals (:entities (:world game))))))
        :else game))

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
                                                        :position {:x 64 :y 64}
                                                        :rotation 0
                                                        :anchor {:x 0.5 :y 0.5}
                                                        :update-fn
                                                          (fn
                                                            [e]
                                                            (update-in e [:rotation] - 0.1)
                                                            )})}))

(def simple-frame (build-pixi-world {1 (build-pixi-sprite {
                                                           :id 1                                                          
                                                           :frame "eggHead.png"
                                                           :position {:x 64 :y 64}
                                                           :rotation 0
                                                           :anchor {:x 0.5 :y 0.5}
                                                           :scale {:x 0.5 :y 0.5}
                                                           :update-fn
                                                           (fn
                                                             [e]
                                                             (-> e 
                                                                 (update-in [:rotation] - 0.1)
                                                                 (assoc-in [:scale :x] (+ 0.85 (* 0.75 (.sin js/Math (/ (.now js/Date) 500)))))
                                                                 (assoc-in [:scale :y] (:x (:scale e))))
                                                             )})}))

(def anim-frame (build-pixi-world {1 (build-pixi-sprite {
                                                           :id 1                                                          
                                                         :frame "alien-green-stand"
                                                         :anim-frame "alien-green-walk-0"
                                                           :position {:x 64 :y 64}
                                                           :rotation 0
                                                           :anchor {:x 0.5 :y 0.5}
                                                           :scale {:x 1.5 :y 1.5}
                                                           :update-fn
                                                           (fn
                                                             [e]
                                                             (-> e 
                                                                 (assoc-in [:anim-frame] (str "alien-green-walk-" (clamp-cycle 0 1 100))))
                                                             )})}))



(def simple-world-b (build-pixi-world {1 (build-pixi-sprite {
                                                        :id 1
                                                        :texture "images/bunny.png"
                                                        :position {:x 64 :y 64}
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


(def reference-worlds {:simple-world simple-world
                       :simple-world-b simple-world-b
                       :simple-frame simple-frame
                       :anim-frame anim-frame
                       })

(defn add-reference-game!
  [reference-id id element]
  (simple-add-game! (build-pixi-game
                     id
                     (build-pixi-view element 128 128 0xaa66ee)
                     (reference-id reference-worlds))))

(defn clone-game!
  [clone-id new-id element]
  (simple-add-game!
   (build-pixi-game
    new-id
    (build-pixi-view
     element
     128 128
     0x55aaee)
    (:world (clone-id @test-games)))))

(defn toggle-pause!
  [id]
  (swap!
   test-games
   #(apply-merge
     %
     id
     (fn
       [game]
       (assoc
         game
         :running
         (not (:running game)))))))

(defn reset-game!
  [id]
  (swap! test-games (fn [games] (apply-merge games id (fn [game] (assoc game :world (:origin game)))))))

(defn js-reset-game
  [reference-name]
  (reset-game! (keyword reference-name)))

(defn js-add-reference-game
  [reference-name name element]
  (add-reference-game! (keyword reference-name) (keyword name) element))

(defn js-clone-game
  [reference-name new-id element]
  (clone-game! (keyword reference-name) (keyword new-id) element))

(defn js-toggle-pause-game
  [reference-name]
  (toggle-pause! (keyword reference-name)))

(defn js-remove-game
  [reference-name]
  (swap! test-games #(dissoc % (keyword reference-name))))

;; setup animation loop
(defn animate[]
  (do
    (swap! test-games run-games)
    (js/requestAnimFrame animate)))

(animate)
