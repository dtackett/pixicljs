;; Renderer system
(ns pixi-cljs.renderer)

; Keep an atom of the current cache of entities setup on the stage
; This is really a shadow of what has been added to the stage
(def render-cache-atom (atom {}))

; Screen size should be defined elsewhere
(defn setup-world!
  "This sets up the pixi.js renderer and stage."
  [world]
  (let [renderer (js/PIXI.autoDetectRenderer 400 300)
        stage (js/PIXI.Stage. 0x66ff99)]
    (do (.appendChild (.-body js/document) (.-view renderer))
      (-> world
          (assoc :stage stage)
          (assoc :renderer renderer))
         )))

(defn set-position [sprite x y]
  (set! (.-position.x sprite) x)
  (set! (.-position.y sprite) y))

(defn set-anchor [sprite x y]
  (set! (.-anchor.x sprite) x)
  (set! (.-anchor.y sprite) y))

(defn set-rotation
  [entity r]
  (set! (.-rotation entity) r))

; Ensure all entities in the cache still exist, if they don't remove them
; Pump entities through and build up the cache
; Give cache and entites to render system
(defn cache-entity-on-stage!
     [entity stage render-cache]
     (let [id (:id entity)]
       (if-not
         (contains? render-cache id)
         (let [sprite (js/PIXI.Sprite. (js/PIXI.Texture.fromImage  (:texture entity)))]
           (do
             (. stage addChild sprite)
             (assoc render-cache id sprite)))
         render-cache)))

(defn update-display
  "Update the pixi-renderer component with the current state"
  [entity render-cache]
  (let [position (:position entity)
        anchor (:anchor entity)
        sprite (get render-cache (:id entity))]
    (set-position
     sprite
     (:x position)
     (:y position))
    (set-anchor
     sprite
     (:x anchor)
     (:y anchor))
    (set-rotation
      sprite
      (:rotation entity))
    render-cache))

(defn pixi-setup-entity
  "Setup the given entity entry for display"
  [entity render-cache stage]
    (update-display
       entity
       (cache-entity-on-stage! entity stage render-cache)))

(defn render-stage
  "This gets the pixi system to return and returns the world. Intended to make dealing with this a bit more composable."
  [game]
  (. (:renderer (:view game)) render (:stage (:view game)))
  game)

(defn remove-from-stage
  "Remove the given entity id from the cache and the stage"
  [render-cache stage entity-id]
  (do
    (if
      (not (nil? (.-parent (get render-cache entity-id))))
      (. stage removeChild (get render-cache entity-id)))
    (dissoc render-cache entity-id))
  )

(defn reconsile-cache
  "This is intended to eventually removed entities that no longer exist from the pixi stage."
  [game render-cache]
  (let [world (:world game)]
    (reduce
     (fn [render-cache entity-id]
       (cond
        (not (contains? (:entities world) entity-id))
        (remove-from-stage render-cache (:stage world) entity-id)
        :else
        render-cache
        ))
     render-cache
     (keys render-cache))))

(defn setup-render-cache
  [game render-cache]
    (reduce
     (fn
       [render-cache entity]
       (pixi-setup-entity entity render-cache (:stage (:view game))))
     render-cache
     (vals (:entities (:world game)))))

(defn setup-view [game]
  (if-not (.contains
            (:parent-element (:view game))
            (.-view (:renderer (:view game))))
          (.appendChild (:parent-element (:view game)) (.-view (:renderer (:view game))))))

; pixi render system
(defn render-game [game]
  (let [cache render-cache-atom]
    (do
      ;; Make sure the view is on the screen
      (setup-view game)

      ;; update and setup cache
      ;; cache should be related to the specific game provided
      (swap! cache (fn [cache]
                     (let [game-cache ((:id game) cache)]
                       (assoc cache (:id game)
                              (->> (if (nil? game-cache)
                                         {}
                                         game-cache)
                                   (reconsile-cache game)
                                   (setup-render-cache game))))))

      ;; render the game
      (-> game
          (render-stage)))))
