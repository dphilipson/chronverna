(ns chronverna.setup
  (:require
    [chronverna.constants :as constants]))

(def initial-state
  {:mode    :setup
   :players [{:name "", :color :red}]})

(defn available-color [state]
  (let [used-color-set (->> (:players state)
                            (map :color)
                            set)]
    (first (drop-while used-color-set constants/colors))))

(defn add-player [state]
  (update state :players conj {:name  "",
                               :color (available-color state)}))

(defn find-indices [pred coll]
  (keep-indexed #(when (pred %2) %1) coll))

(defn set-player-color-no-swap [state index color]
  (assoc-in state [:players index :color] color))

(defn set-player-color [state index color]
  (let [current-color (get-in state [:players index :color])
        old-index (first (find-indices #(= (:color %) color) (:players state)))]
    (if old-index
      (-> state
          (set-player-color-no-swap index color)
          (set-player-color-no-swap old-index current-color))
      (set-player-color-no-swap state index color))))

(defn set-player-name [state index name]
  (assoc-in state [:players index :name] name))

(defn remove-vec-element [v i]
  (vec (concat (subvec v 0 i) (subvec v (inc i)))))

(defn remove-player [state index]
  (update state :players remove-vec-element index))