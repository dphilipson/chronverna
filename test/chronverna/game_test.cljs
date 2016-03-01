(ns chronverna.game-test
  (:require
    [cljs.test :refer-macros [deftest testing is]]
    [chronverna.game :as game]))

; Test helpers

(defn test-player [name color]
  {:name name, :color color, :remaining 2, :family-size 2, :time-used-ms 0})

(def ms-scarlet (test-player "Miss Scarlet" :red))
(def mr-green (test-player "Mr. Green" :green))
(def prof-plum (test-player "Professor Plum" :purple))

(def base-game-state
  {:current-index   0
   :starting-index  0
   :between-rounds? false
   :round           1
   :players         [ms-scarlet mr-green prof-plum]})

(def base-meta-state
  {:paused            false
   :last-timestamp-ms nil
   :history           []
   :history-index     0
   :game-state        base-game-state})

(defn game-state [& kvs]
  (merge base-game-state (apply hash-map kvs)))

(defn meta-state [& kvs]
  (merge base-meta-state (apply hash-map kvs)))

; Tests

(deftest test-new-game-state
  (testing "It should start each player with two dwarves"
    (let [players [{:name "Miss Scarlet", :color :red}
                   {:name "Mr. Green", :color :green}]
          state (game/new-game-state players)
          expected-game-state {:current-index   0
                               :starting-index  0
                               :between-rounds? true
                               :round           1
                               :players         [{:name         "Miss Scarlet"
                                                  :color        :red
                                                  :remaining    2
                                                  :family-size  2
                                                  :time-used-ms 0}
                                                 {:name         "Mr. Green"
                                                  :color        :green
                                                  :remaining    2
                                                  :family-size  2
                                                  :time-used-ms 0}]}
          expected {:mode              :game
                    :paused?           false
                    :last-timestamp-ms nil
                    :history           []
                    :history-index     0
                    :game-state        expected-game-state}]
      (is (= state expected)))))

(deftest test-start-round
  (testing "It should start the round"
    (let [initial-state (game-state :between-rounds? true)
          updated-state (game/start-round initial-state)
          expected (game-state :between-rounds? false)]
      (is (= updated-state expected)))))

(deftest test-player-selected-next
  (testing "It should expend a dwarf and move to next player"
    (let [initial-state base-game-state
          updated-state (game/player-selected-next initial-state)
          expected-scarlet (assoc ms-scarlet :remaining 1)
          expected-state (game-state :players [expected-scarlet mr-green prof-plum]
                                     :current-index 1)]
      (is (= updated-state expected-state))))
  (testing "It should wrap around after last player"
    (let [initial-state (game-state :current-index 2)
          updated-state (game/player-selected-next initial-state)
          expected-plum (assoc prof-plum :remaining 1)
          expected-state (game-state :players [ms-scarlet mr-green expected-plum]
                                     :current-index 0)]
      (is (= updated-state expected-state))))
  (testing "It should skip players with no remaining dwarves"
    (let [dwarfless-green (assoc mr-green :remaining 0)
          initial-state (game-state :players [ms-scarlet dwarfless-green prof-plum])
          updated-state (game/player-selected-next initial-state)
          expected-scarlet (assoc ms-scarlet :remaining 1)
          expected-state (game-state :players [expected-scarlet dwarfless-green prof-plum]
                                     :current-index 2)]
      (is (= updated-state expected-state))))
  (testing "It should prepare next round if no player has dwarves remaining"
    (let [initial-state (game-state :players [(assoc ms-scarlet :remaining 1)
                                              (assoc mr-green :remaining 0)
                                              (assoc prof-plum :remaining 0)]
                                    :starting-index 2
                                    :round 2)
          updated-state (game/player-selected-next initial-state)
          expected-state (game-state :current-index 2
                                     :starting-index 2
                                     :between-rounds? true
                                     :round 3)]
      (is (= updated-state expected-state)))))

(deftest test-player-added-dwarf
  (testing "It should add an unavailable dwarf, use a dwarf, and move to next player"
    (let [initial-state base-game-state
          updated-state (game/player-added-dwarf initial-state)
          expected-scarlet (assoc ms-scarlet :remaining 1
                                             :family-size 3)
          expected-state (game-state :players [expected-scarlet mr-green prof-plum]
                                     :current-index 1)]
      (is (= updated-state expected-state)))))

(deftest test-player-took-starting-player
  (testing "It should set starting-player, use a dwarf, and move to next player"
    (let [initial-state (game-state :players [ms-scarlet mr-green prof-plum]
                                    :current-index 1)
          updated-state (game/player-took-starting-player initial-state)
          expected-green (assoc mr-green :remaining 1)
          expected-state (game-state :players [ms-scarlet expected-green prof-plum]
                                     :current-index 2
                                     :starting-index 1)]
      (is (= updated-state expected-state)))))

(deftest test-advance-to-time
  (testing "Should add to current player when in play"
    (let [mr-green-with-time (assoc mr-green :time-used-ms 200)
          initial-state (meta-state :paused? false
                                    :last-timestamp-ms 1000
                                    :game-state
                                    (game-state :players [ms-scarlet mr-green-with-time prof-plum]
                                                :current-index 1))
          updated-state (game/advance-to-time initial-state 1300)
          expected-green (assoc mr-green :time-used-ms 500)
          expected-state (meta-state :paused? false
                                     :last-timestamp-ms 1300
                                     :game-state
                                     (game-state :players [ms-scarlet expected-green prof-plum]
                                                 :current-index 1))]
      (is (= updated-state expected-state))))
  (testing "Should not update player time on first update"
    (let [state (game/advance-to-time base-meta-state 1300)
          expected-state (meta-state :last-timestamp-ms 1300)]
      (is (= state expected-state))))
  (testing "Should not add to time when paused"
    (let [initial-state (meta-state :paused? true
                                    :last-timestamp-ms 1000)
          updated-state (game/advance-to-time initial-state 1300)
          expected-state (meta-state :paused? true
                                     :last-timestamp-ms 1300)]
      (is (= updated-state expected-state))))
  (testing "Should not add to time when between rounds"
    (let [initial-state (meta-state :last-timestamp-ms 1000
                                    :game-state (game-state :between-rounds? true))
          updated-state (game/advance-to-time initial-state 1300)
          expected-state (assoc initial-state :last-timestamp-ms 1300)]
      (is (= updated-state expected-state)))))
