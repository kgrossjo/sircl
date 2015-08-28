(ns sircl.index-test
  (:require [clojure.test :refer :all]
            [sircl.util :refer :all]))

(deftest map-hash-test
  (testing "simple"
    (is (= (map-hash (fn [x] (* x x)) (list 1 2 3 4))
           {1 1, 2 4, 3 9, 4 16}))))
