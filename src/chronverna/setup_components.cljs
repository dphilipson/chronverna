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

; Add player button

(defn add-player-button []
  [:div.add-player-button-container>button.add-player-button.btn.btn-default
   [:span.glyphicon.glyphicon-plus]
   " Add player"])

; Start button

(defn start-game-button []
  [:div.start-game-button-container>button.btn.btn-primary.btn-lg nil "Start Game"])

; Main component

(defn main [state actions]
  [:div.chroverna.no-select
   [:div.setup-wrapper
    [:h1 "Chronverna"]
    [:h4 "Player Select"]
    [:div.player-select-area
     (for [i (range constants/max-players)]
       ^{:key i} [player-select])]
    [add-player-button]
    [start-game-button]]])