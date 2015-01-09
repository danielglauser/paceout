(ns paceout.test.main
  "Clojure.test tests for the main control flow of the paceout account
  number parser."
  (:require [clojure.test :refer :all]
            [paceout.main :refer :all]))

(deftest test-main-usage
  (testing "Basic smoke test of usage text."
    (with-redefs [exit (constantly 0)]
      (let [usage-txt (with-out-str (-main "-h"))]
        (is (re-find #"Usage:" usage-txt))))))
