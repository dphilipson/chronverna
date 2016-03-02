(ns chronverna.setup-components
  (:require
    [chronverna.constants :as constants]
    [chronverna.util :as util]))


; Player select

(defn value-from-change-event [e]
  (-> e .-target .-value))

(defn wrap-on-change-for-event
  "Takes a handler which receives the new faction/color value and transforms it to receive an
  on-change event instead."
  [on-change]
  (fn [e] (on-change (value-from-change-event e))))

(defn color-select [player on-change]
  [:select.color-select.form-control
   {:value     (util/title (:color player))
    :on-change (wrap-on-change-for-event (comp on-change util/from-title))}
   (for [color constants/colors]
     (let [color-title (util/title color)]
       ^{:key color-title} [:option {:value color-title} color-title]))])

(defn player-select [i state {:keys [on-set-player-name on-set-player-color on-remove-player]}]
  (let [player (get-in state [:players i])]
    [:div.player-select-row
     [:input.name-input.form-control
      {:type        "text"
       :placeholder (str "Player " (inc i))
       :value       (:name player)
       :on-change   (wrap-on-change-for-event (partial on-set-player-name i))}]
     [color-select player (partial on-set-player-color i)]
     [:button.remove-player-button.btn.btn-default {:on-click (partial on-remove-player i)
                                                    :disabled (= (count (:players state)) 1)}
      [:span.glyphicon.glyphicon-remove]]]))

; Add player button

(defn add-player-button [on-add-player]
  [:div.add-player-button-container>button.add-player-button.btn.btn-default
   {:on-click on-add-player}
   [:span.glyphicon.glyphicon-plus]
   " Add player"])

; Start button

(defn start-game-button [on-start-game]
  [:div.start-game-button-container>button.btn.btn-primary.btn-lg
   {:on-click on-start-game}
   "Start Game"])

; Main component

(defn main [state {:keys [on-add-player on-start-game] :as actions}]
  (let [player-count (count (:players state))]
    [:div.chroverna.no-select
     [:div.setup-wrapper
      [:h1 "Chronverna"]
      [:h4 "Player Select"]
      [:div.player-select-area
       (for [i (range player-count)]
         ^{:key i} [player-select i state actions])]
      (when (< player-count constants/max-players)
        [add-player-button on-add-player])
      [start-game-button on-start-game]]]))
