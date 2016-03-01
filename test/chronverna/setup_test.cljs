(ns chronverna.setup-test
  (:require
    [cljs.test :refer-macros [deftest testing is]]
    [chronverna.setup :as setup]))

(defn setup-state [players]
  {:mode :setup, :players players})

; Test players

(defn anonymous [color]
  {:name "", :color color})

(def ms-scarlet
  {:name "Miss Scarlet", :color :red})

(def mr-green
  {:name "Mr. Green", :color :green})

(def prof-plum
  {:name "Professor Plum", :color :purple})

(deftest test-initial-state
  (testing "It should initialize with one player"
    (is (= setup/initial-state (setup-state [(anonymous :red)])))))

(deftest test-add-player
  (testing "It should add a new player"
    (let [initial-state (setup-state [mr-green prof-plum])
          updated-state (setup/add-player initial-state)
          expected-state (setup-state [mr-green prof-plum (anonymous :red)])]
      (is (= updated-state expected-state))))
  (testing "It should choose an available color"
    (let [initial-state (setup-state [ms-scarlet])
          updated-state (setup/add-player initial-state)
          expected-state (setup-state [ms-scarlet (anonymous :yellow)])]
      (is (= updated-state expected-state)))))

(deftest test-set-player-color
  (testing "It should set a player's color"
    (let [initial-state (setup-state [mr-green prof-plum])
          updated-state (setup/set-player-color initial-state 1 :red)
          expected-state (setup-state [mr-green
                                       {:name "Professor Plum", :color :red}])]
      (is (= updated-state expected-state))))
  (testing "It should swap colors if selection is already used"
    (let [initial-state (setup-state [mr-green prof-plum])
          updated-state (setup/set-player-color initial-state 1 :green)
          expected-state (setup-state [{:name "Mr. Green", :color :purple}
                                       {:name "Professor Plum", :color :green}])]
      (is (= updated-state expected-state)))))

(deftest test-set-player-name
  (testing "It should set a player's name"
    (let [initial-state (setup-state [mr-green prof-plum])
          updated-state (setup/set-player-name initial-state 1 "Christian Grey")
          expected-state (setup-state [mr-green
                                       {:name "Christian Grey", :color :purple}])]
      (is (= updated-state expected-state)))))

(deftest test-remove-player
  (testing "It should remove a player"
    (let [initial-state (setup-state [ms-scarlet mr-green prof-plum])
          updated-state (setup/remove-player initial-state 1)
          expected-state (setup-state [ms-scarlet prof-plum])]
      (is (= updated-state expected-state)))))
