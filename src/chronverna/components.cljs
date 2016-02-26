(ns chronverna.components
  (:require
    [chronverna.setup-components :as setup]))

(defn main [app-state-atom actions]
  (let [{:keys [mode] :as state} @app-state-atom]
    (case mode
      :setup [setup/main state actions])))
