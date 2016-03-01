(ns chronverna.constants
  (:require
    [clojure.string :as str]))

(def max-players 7)

(def colors
  [:red :yellow :green :light-blue :dark-blue :purple :white])

(defn title [keyword]
  (->>
    (-> keyword name (str/split "-"))
    (map str/capitalize)
    (str/join " ")))
