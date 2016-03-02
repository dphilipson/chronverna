(ns chronverna.util
  (:require
    [clojure.string :as str]))

(defn title [keyword]
  (->>
    (-> keyword name (str/split "-"))
    (map str/capitalize)
    (str/join " ")))

(defn from-title [title]
  (-> title
      str/lower-case
      (str/replace " " "-")
      keyword))

(defn rotate-seq [i s]
  (concat (drop i s) (take i s)))
