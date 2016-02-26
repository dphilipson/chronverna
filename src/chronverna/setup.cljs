(ns chronverna.setup
  (:require
    [chronverna.constants :as constants]))

(def initial-state
  {:mode :setup
   :player-colors (vec (repeat constants/max-players nil))})

(defn set-player-color [state index color]
  (assoc-in state [:player-colors index] color))
