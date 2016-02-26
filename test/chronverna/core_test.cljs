(ns ^:figwheel-always chronverna.core-test
  (:require
    [cljs.test :refer-macros [deftest testing is]]
    [figwheel.client :as fw]
    [chronverna.setup-test]
    [chronverna.game-test]
    [chronverna.test-formatter]))

;; Test runner

(defn run-tests []
  (.clear js/console)
  (cljs.test/run-all-tests #"chronverna.*-test"))

(run-tests)

;; FW connection is optional in order to simply run tests,
;; but is needed to connect to the FW repl and to allow
;; auto-reloading on file-save
(fw/start {:websocket-url "ws://localhost:3449/figwheel-ws"
           :build-id      "test"})
