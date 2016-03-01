(ns chronverna.game-test
  (:require
    [cljs.test :refer-macros [deftest testing is]]
    [chronverna.game :as game]))

; Test helpers

(defn test-player [name color]
  {:name name, :color color, :remaining 2, :family-size 2})

(def ms-scarlet (test-player "Miss Scarlet" :red))
(def mr-green (test-player "Mr. Green" :green))
(def prof-plum (test-player "Professor Plum" :purple))

(defn game-state [players]
  {:current-index   0
   :starting-index  0
   :between-rounds? false
   :round           1
   :players         players})

; Tests

(deftest test-new-game-state
  (testing "It should start each player with two dwarves"
    (let [players [{:name "Miss Scarlet", :color :red}
                   {:name "Mr. Green", :color :green}]
          state (game/new-game-state players)
          expected {:current-index   0
                    :starting-index  0
                    :between-rounds? true
                    :round           1
                    :players         [{:name        "Miss Scarlet"
                                       :color       :red
                                       :remaining   2
                                       :family-size 2}
                                      {:name        "Mr. Green"
                                       :color       :green
                                       :remaining   2
                                       :family-size 2}]}]
      (is (= state expected)))))

(deftest test-start-round
  (testing "It should start the round"
    (let [initial-state (assoc (game-state [ms-scarlet mr-green]) :between-rounds? true)
          updated-state (game/start-round initial-state)
          expected (assoc (game-state [ms-scarlet mr-green]) :between-rounds? false)]
      (is (= updated-state expected)))))

(deftest test-player-selected-next
  (testing "It should expend a dwarf and move to next player"
    (let [initial-state (game-state [ms-scarlet mr-green prof-plum])
          updated-state (game/player-selected-next initial-state)
          expected-scarlet (assoc ms-scarlet :remaining 1)
          expected-state (assoc
                           (game-state [expected-scarlet mr-green prof-plum])
                           :current-index 1)]
      (is (= updated-state expected-state))))
  (testing "It should wrap around after last player"
    (let [initial-state (assoc (game-state [ms-scarlet mr-green prof-plum]) :current-index 2)
          updated-state (game/player-selected-next initial-state)
          expected-plum (assoc prof-plum :remaining 1)
          expected-state (assoc (game-state [ms-scarlet mr-green expected-plum])
                           :current-index 0)]
      (is (= updated-state expected-state))))
  (testing "It should skip players with no remaining dwarves"
    (let [dwarfless-green (assoc mr-green :remaining 0)
          initial-state (game-state [ms-scarlet dwarfless-green prof-plum])
          updated-state (game/player-selected-next initial-state)
          expected-scarlet (assoc ms-scarlet :remaining 1)
          expected-state (assoc (game-state [expected-scarlet dwarfless-green prof-plum])
                           :current-index 2)]
      (is (= updated-state expected-state))))
  (testing "It should prepare next round if no player has dwarves remaining"
    (let [initial-state {:current-index   0
                         :starting-index  2
                         :between-rounds? false
                         :round           2
                         :players         [(assoc ms-scarlet :remaining 1)
                                           (assoc mr-green :remaining 0)
                                           (assoc prof-plum :remaining 0)]}
          updated-state (game/player-selected-next initial-state)
          expected-state {:current-index   2
                          :starting-index  2
                          :between-rounds? true
                          :round           3
                          :players         [ms-scarlet mr-green prof-plum]}]
      (is (= updated-state expected-state)))))
