(ns chronverna.game)

(def initial-family-size 2)

(defn new-game-state [players]
  (let [new-player (partial merge {:remaining    initial-family-size
                                   :family-size  initial-family-size
                                   :time-used-ms 0})]
    {:mode              :game
     :paused?           false
     :last-timestamp-ms nil
     :history           []
     :history-index     0
     :game-state        {:current-index   0
                         :starting-index  0
                         :between-rounds? true
                         :round           1
                         :players         (mapv new-player players)}}))

(defn start-round [game-state]
  (assoc game-state :between-rounds? false))

(defn rotate-seq [s i]
  (concat (drop i s) (take i s)))

(defn next-player-index
  "Returns the index of the player whose turn is after the current player, or nil if it would be the
   end of the round."
  [{:keys [players current-index] :as game-state}]
  (let [indexed-players (map-indexed vector players)
        players-after-current (rotate-seq indexed-players (inc current-index))
        [index _] (first (filter (fn [[_ player]]
                                   (pos? (:remaining player)))
                                 players-after-current))]
    ; If the next player is the current player and they are on their last dwarf, return nil.
    (if (and (= index current-index)
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

(defn advance-to-time [{:keys [paused? last-timestamp-ms game-state] :as state} timestamp-ms]
  (let [{:keys [between-rounds? current-index]} game-state
        time-delta-ms (if last-timestamp-ms
                         (- timestamp-ms last-timestamp-ms)
                         0)
        new-time-state (assoc state :last-timestamp-ms timestamp-ms)]
    (if (or paused? between-rounds?)
      new-time-state
      (update-in
        new-time-state
        [:game-state :players current-index :time-used-ms]
        + time-delta-ms))))
