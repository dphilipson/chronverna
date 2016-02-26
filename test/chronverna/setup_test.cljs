(ns chronverna.setup-test
  (:require
    [cljs.test :refer-macros [deftest testing is]]
    [chronverna.setup :as setup]))

(deftest test-initial-state
  (testing "It should initialize with all players absent"
    (is (= setup/initial-state {:mode          :setup
                                :player-colors [nil nil nil nil nil nil nil]}))))

(deftest test-set-player-color
  (testing "It should set the color for the selected player"
    (is (= (setup/set-player-color setup/initial-state 1 :green)
           {:mode          :setup
            :player-colors [nil :green nil nil nil nil nil]})))
  (testing "It should set the color to nil if requested"
    (let [initial-state {:mode    :setup
                         :player-colors [:red :green :blue nil nil nil nil]}
          updated-state (setup/set-player-color initial-state 1 nil)
          expected-state {:mode :setup
                          :player-colors [:red nil :blue nil nil nil nil]}]
      (is (= updated-state expected-state)))))