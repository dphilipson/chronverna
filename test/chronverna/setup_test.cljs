(ns chronverna.setup-test
  (:require
    [cljs.test :refer-macros [deftest testing is]]))

(deftest test-hello-world
  (testing "It should run the tests"
    (is (= (+ 1 2) 3))))