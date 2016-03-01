(ns chronverna.game)

(defn new-game-state [players]
  {:current-index   0
   :starting-index  0
   :between-rounds? true
   :round           1
   :players         (vec (map
                           (partial merge {:remaining 2, :family-size 2})
                           players))})

(defn start-round [game-state]
  (assoc game-state :between-rounds? false))

(defn rotate-seq [s i]
  (concat (drop i s) (take i s)))

(defn next-player-index
  "Returns the index of the player whose turn is after the current player, or nil if it would be the
   end of the round."
  [game-state]
  (let [indexed-players (map-indexed vector (:players game-state))
        players-after-current (rotate-seq indexed-players (inc (:current-index game-state)))
        [index _] (first (filter (fn [[_ player]]
                                   (pos? (:remaining player)))
                                 players-after-current))]
    ; If the next player is the current player and they are on their last dwarf, return nil.
    (if (and (= index (:current-index game-state))
             (= (get-in game-state [:players index :remaining]) 1))
      nil
      index)))

(defn end-round [game-state]
  (-> game-state
      (assoc :current-index (:starting-index game-state)
             :between-rounds? true)
      (update :round inc)
      (update :players (partial map (fn [p]
                                      (assoc p :remaining (:family-size p)))))))

(defn player-selected-next [game-state]
  (if-let [next-index (next-player-index game-state)]
    (-> game-state
        (assoc :current-index next-index)
        (update-in [:players (:current-index game-state) :remaining] dec))
    (end-round game-state)))

(defn player-added-dwarf [game-state]
  (-> game-state
      (update-in [:players (:current-index game-state) :family-size] inc)
      player-selected-next))

(defn player-took-starting-player [game-state]
  (-> game-state
      (assoc :starting-index (:current-index game-state))
      player-selected-next))
