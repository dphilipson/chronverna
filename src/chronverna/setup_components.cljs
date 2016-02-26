(ns chronverna.setup-components
  (:require
    [chronverna.constants :as constants]))


; Player select

(defn color-select []
  [:select.form-control nil
   (for [color constants/colors]
     ^{:key color} [:option {:value color} (constants/title color)])])

(defn player-select []
  [:div.row
   [:div.name-input-container.col-xs-8>input.form-control {:type        "text"
                                                           :placeholder "Player 1"}]
   [:div.color-select-container.col-xs-4 [color-select]]])

; Start button

(defn start-game-button []
  [:button.start-game-button.btn.btn-primary.btn-lg nil "Start Game"])

; Main component

(defn main [state actions]
  [:div.chroverna
   [:div.player-select-wrapper
    [:h1 "Chronverna"]
    [:h4 "Player Select"]
    (for [i (range constants/max-players)]
      ^{:key i} [player-select])
    [start-game-button]]])