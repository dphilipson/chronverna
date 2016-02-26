(ns chronverna.core
  (:require [reagent.core :as reagent :refer [atom]]
            [chronverna.setup :as setup]
            [chronverna.game :as game]
            [cljs.reader :as reader]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(def schema-version "0")
(def schema-key "chv-schema-version")
(def state-key "chv-state")

(defn update-version-clear-state-if-wrong! []
  (let [saved-schema-version (.getItem js/localStorage schema-key)]
    (when (not= saved-schema-version schema-version)
      (.removeItem js/localStorage state-key)
      (.setItem js/localStorage schema-key schema-version))))

(defonce app-state-atom
         (do (update-version-clear-state-if-wrong!)
             (let [saved-state-edn (.getItem js/localStorage state-key)
                   saved-state (when saved-state-edn (reader/read-string saved-state-edn))]
               (atom (or saved-state setup/initial-state)))))

(defonce app-state (atom {:text "Hello world!"}))

(defn hello-world []
  [:h1 (:text @app-state)])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
