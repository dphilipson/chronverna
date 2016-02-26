(ns chronverna.setup)

(def max-players 7)

(def initial-state
  {:mode :setup
   :player-colors (vec (repeat max-players nil))})

(defn set-player-color [state index color]
  (assoc-in state [:player-colors index] color))
