(ns paceout.test.main
  "Clojure.test tests for the main control flow of the paceout account
  number parser."
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [paceout.main :refer :all]))

(deftest test-main-usage
  (testing "Basic smoke test of usage text."
    (let [exit-code (atom nil)
          message (atom "")]
      (with-redefs [exit (fn [status & [msg]]
                           (reset! exit-code status)
                           (when msg (reset! message msg)))]
        (-main "-h")
        (is (= 0 @exit-code) "-main should honor the help flag.")
        (is (re-find #"Usage:" @message))))))

(deftest test-required-arg
  (testing "Argument parsing"
      (let [exit-code (atom nil)
            message (atom "")]
        (with-redefs [exit (fn [status & [msg]]
                             (reset! exit-code status)
                             (when msg (reset! message msg)))]
          (-main)
          (is (= 1 @exit-code) "-main should require one argument.")
          (is (re-find #"Usage:" @message))))))

(deftest test-cli-parse-errors
  (testing "CLI parse errors are reported properly."
    (let [exit-code (atom nil)
          message (atom "")]
      (with-redefs [exit (fn [status & [msg]]
                           (reset! exit-code status)
                           (when msg (reset! message msg)))]
        (-main "-wat")
        (is (= 1 @exit-code) "there is no -wat flag")
        (is (re-find #"Unknown option" @message))))))

