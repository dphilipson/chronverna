(ns chronverna.game-components
  (:require
    [chronverna.constants :as constants]
    [chronverna.util :as util]))

(defn current-player [game-state]
  (get-in game-state [:players (:current-index game-state)]))

(defn active-players
  "Returns a list of players with upcoming actions. Does not include the current player."
  [game-state]
  (->> (:players game-state)
       (util/rotate-seq (:current-index game-state))
       rest
       (filter (comp pos? :remaining))
       vec))

(defn done-players [game-state]
  (->> (:players game-state)
       (util/rotate-seq (:starting-index game-state))
       (filter (comp zero? :remaining))
       vec))

(defn game-over? [game-state]
  (> (:round game-state) constants/num-rounds))

;; Meta buttons

(defn reset-button [on-reset]
  [:button.reset-button.btn.btn-danger.btn-lg {:on-click on-reset}
   [:span.glyphicon.glyphicon-remove]])

(defn undo-button [{:keys [history-index]} on-undo]
  (let [enabled? (pos? history-index)
        action (if enabled? on-undo nil)
        disabled-class (if enabled? nil :disabled)]
    [:button.undo-button.btn.btn-default.btn-lg {:class disabled-class :on-click action}
     [:span.glyphicon.glyphicon-step-backward]]))

(defn redo-button [{:keys [history history-index]} on-redo]
  (let [enabled? (< history-index (count history))
        action (if enabled? on-redo nil)
        disabled-class (if enabled? nil :disabled)]
    [:button.redo-button.btn.btn-default.btn-lg {:class disabled-class :on-click action}
     [:span.glyphicon.glyphicon-step-forward]]))

(defn pause-button [{:keys [paused?]} {:keys [on-pause on-unpause]}]
  (let [action (if paused? on-unpause on-pause)
        glyphicon (if paused? :glyphicon-play :glyphicon-pause)]
    [:button.pause-button.btn.btn-default.btn-lg {:on-click action}
     [:span.glyphicon {:class glyphicon}]]))

(defn meta-button-area [state {:keys [on-reset on-undo on-redo] :as actions}]
  [:div.meta-button-area
   [reset-button on-reset]
   [:div.spacer]
   [undo-button state on-undo]
   [redo-button state on-redo]
   [pause-button state actions]])

;; Clock

(defn two-digit-str [n]
  (if (< n 10)
    (str "0" n)
    (str n)))

(defn format-time [ms]
  (let [total-seconds (quot ms 1000)
        minutes (quot total-seconds 60)
        seconds (rem total-seconds 60)
        seconds-str (two-digit-str seconds)]
    (str minutes ":" seconds-str)))

(defn subsecond-component [ms]
  (str "." (-> ms (quot 10) (rem 100) two-digit-str)))

(defn main-clock [time-used-ms]
  [:div.clock-area
   [:p
    [:span.clock (format-time time-used-ms)]
    [:span.clock-subsecond (subsecond-component time-used-ms)]]])

;; Buttons

(defn start-round-button [round on-start-round]
  (let [text (str "Start Round " round)]
    [:button.start-round-button.btn.btn-primary.btn-lg {:on-click on-start-round} text]))

(defn game-over-button []
  [:button.game-over-button.btn.btn-primary.btn-lg.disabled "Game Over"])

(defn special-action-buttons [{:keys [on-grow-family on-take-start-player]}]
  [:div.special-action-row
   [:button.grow-family-button.btn.btn-default.btn-lg
    {:on-click on-grow-family}
    "Grow Family"]
   [:button.take-start-player-button.btn.btn-default.btn-lg
    {:on-click on-take-start-player}
    "Take Start Player"]])

(defn next-button [on-next]
  [:button.next-button.btn.btn-primary.btn-lg {:on-click on-next} "Next Player"])

(defn gameplay-buttons [actions]
  [:div.gameplay-button-container
   [special-action-buttons actions]
   [next-button (:on-next actions)]])

(defn button-area [{:keys [between-rounds? round] :as game-state}
                   {:keys [on-start-round] :as actions}]
  [:div.button-area
   (cond
     (game-over? game-state) [game-over-button]
     between-rounds? [start-round-button round on-start-round]
     :else [gameplay-buttons actions])])

;; Current player area

(defn current-player-area [game-state actions]
  (let [player (get-in game-state [:players (:current-index game-state)])
        {:keys [index name remaining family-size time-used-ms]} player
        start-player? (= index (:starting-index game-state))]
    [:div.current-player-area
     [:div.player-label-area
      [:p.active-player-name name]
      [:p.active-player-status (str remaining "/" family-size " actions left"
                                    (when start-player? ", Starting Player"))]]
     [main-clock time-used-ms]
     [button-area game-state actions]]))

;; Active player area

(defn player-list-item [{:keys [name color remaining family-size time-used-ms]} is-starting?]
  [:div.player-item {:class (cljs.core/name color)}
   [:p.player-label (str name " (" remaining "/" family-size
                         (when is-starting? ", Start")
                         ")")]
   [:p.timer (format-time time-used-ms)]])

(defn active-players-area [{:keys [starting-index] :as game-state}]
  (let [players (active-players game-state)]
    (when (seq players)
      [:div.active-players-area
       [:p.player-list-label "Next:"]
       (for [{:keys [index] :as player} players]
         ^{:key index}
         [player-list-item player (= index starting-index)])])))

;; Passed player area

(defn done-players-area [{:keys [starting-index] :as game-state}]
  (let [players (done-players game-state)]
    (when (seq players)
      [:div.done-players-area
       [:p.player-list-label "Done:"]
       (for [{:keys [index] :as player} players]
         ^{:key index}
         [player-list-item player (= index starting-index)])])))

(defn main [state actions]
  (let [game-state (:game-state state)]
    [:div.chronverna.no-select {:class (name (:color (current-player game-state)))}
     [meta-button-area state actions]
     [current-player-area game-state actions]
     [active-players-area game-state]
     [done-players-area game-state]]))
