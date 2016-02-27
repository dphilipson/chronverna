(ns chronverna.setup-components
  (:require
    [chronverna.constants :as constants]))


; Player select

(defn color-select []
  [:select.color-select.form-control nil
   (for [color constants/colors]
     ^{:key color} [:option {:value color} (constants/title color)])])

(defn player-select []
  [:div.player-select-row
   [:input.name-input.form-control {:type        "text"
                                    :placeholder "Player 1"}]
   [color-select]
   [:button.remove-player-button.btn.btn-default>span.glyphicon.glyphicon-remove]])

; Start button

(defn start-game-button []
  [:button.start-game-button.btn.btn-primary.btn-lg nil "Start Game"])

; Main component

(defn main [state actions]
  [:div.chroverna.no-select
   [:div.setup-wrapper
    [:h1 "Chronverna"]
    [:h4 "Player Select"]
    [:div.player-select-area
     (for [i (range constants/max-players)]
       ^{:key i} [player-select])]
    [start-game-button]]])