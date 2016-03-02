(ns chronverna.game
  (:require
    [chronverna.constants :as constants]
    [chronverna.util :as util]))

(defn new-game-state [players]
  (let [new-player (fn [index player]
                     (merge player {:index        index
                                    :remaining    constants/initial-family-size
                                    :family-size  constants/initial-family-size
                                    :time-used-ms 0}))]
    {:mode              :game
     :paused?           false
     :last-timestamp-ms nil
     :history           []
     :history-index     0
     :game-state        {:current-index   0
                         :starting-index  0
                         :between-rounds? true
                         :round           1
                         :players         (vec (map-indexed new-player players))}}))

(defn start-round [game-state]
  (assoc game-state :between-rounds? false))

(defn next-player-index
  "Returns the index of the player whose turn is after the current player, or nil if it would be the
   end of the round."
  [{:keys [players current-index] :as game-state}]
  (let [indexed-players (map-indexed vector players)
        players-after-current (util/rotate-seq (inc current-index) indexed-players)
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
      (update :players (partial mapv (fn [p]
                                       (assoc p :remaining (:family-size p)))))))

(defn player-selected-next [game-state]
  (if-let [next-index (next-player-index game-state)]
    (-> game-state
        (assoc :current-index next-index)
        (update-in [:players (:current-index game-state) :remaining] dec))
    (end-round game-state)))

(defn player-grew-family [game-state]
  (-> game-state
      (update-in [:players (:current-index game-state) :family-size] inc)
      player-selected-next))

(defn player-took-start-player [game-state]
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

; History

; Enough for seven players averaging four moves per round. Should be enough.
(def max-history-size 400)

(defn trim-history [{:keys [history history-index] :as state}]
  (let [trim-amount (max 0 (- (count history) max-history-size))]
    (assoc state :history (subvec history trim-amount)
                 :history-index (- history-index trim-amount))))

(defn update-game-state-add-history
  [{:keys [game-state history history-index] :as state} f & args]
  (-> state
      (assoc :game-state (apply f game-state args)
             :history (-> history (subvec 0 history-index) (conj game-state))
             :history-index (inc history-index))
      trim-history))

(defn undo [{:keys [game-state history history-index] :as state}]
  (assoc state :history (if (= (count history) history-index)
                          (conj history game-state)
                          history)
               :history-index (dec history-index)
               :game-state (history (dec history-index))))

(defn redo [{:keys [history history-index] :as state}]
  (let [new-index (inc history-index)
        history-size (count history)]
    (assoc state :history (if (= new-index (dec history-size))
                            (subvec history 0 (dec history-size))
                            history)
                 :history-index new-index
                 :game-state (history new-index))))
