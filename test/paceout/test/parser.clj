(ns paceout.test.parser
  "Clojure.test tests for the parser namespace."
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [paceout.parser :refer :all]))

(deftest test-checksum
  (is (= 0 (checksum "000000051")))
  (is (= 0 (checksum "457508000")))
  (is (= 6 (checksum "999508011")))
  (is (= 3 (checksum "457508011"))))

(deftest test-parse-column
  (is (= [["a" "d"] ["bc" "ef"]]
         (parse-column 1 ["abc" "def"])))
  (is (= [["ab" "de"] ["c" "f"]]
         (parse-column 2 ["abc" "def"])))
  (is (= [["abc" "def"] ["" ""]]
         (parse-column 3 ["abc" "def"]))))

(deftest test-safe-subs
  (is (= "d" (safe-subs "abcd" 3)))
  (is (= "" (safe-subs "abc" 3)))
  (is (= nil (safe-subs nil 3))))

(deftest test-parse-account-number*
  (is (= "000000000"
         (parse-account-number* [" _  _  _  _  _  _  _  _  _ "
                                 "| || || || || || || || || |"
                                 "|_||_||_||_||_||_||_||_||_|"])))
  (is (= "912345678"
         (parse-account-number* [" _     _  _     _  _  _  _ "
                                 "|_|  | _| _||_||_ |_   ||_|"
                                 "  |  ||_  _|  | _||_|  ||_|"])))
  (is (= "" (parse-account-number* nil))))

(deftest test-parse-account-numbers
  (with-open [rdr (-> (io/resource "sample_account_numbers.txt") io/reader)
              wtr (java.io.StringWriter.)]
    (parse-account-numbers rdr wtr)
    (let [account-numbers (.toString wtr)]
      (is (= (str "000000000\n111111111\n333333333\n444444444\n555555555\n"
                  "666666666\n777777777\n888888888\n999999999\n123456789\n")
             account-numbers)))))

(deftest test-parse-valid-account-numbers
  (with-open [rdr (-> (io/resource "valid_account_numbers.txt") io/reader)
              wtr (java.io.StringWriter.)]
    (parse-account-numbers rdr wtr #(= 0 (checksum %)))
    (let [account-numbers (.toString wtr)]
      (is (= "711111111\n123456789\n490867715\n" account-numbers)))))

(deftest test-parse-invalid-account-numbers
  (with-open [rdr (-> (io/resource "invalid_account_numbers.txt") io/reader)
              wtr (java.io.StringWriter.)]
    (parse-account-numbers rdr wtr #(= 0 (checksum %)) true)
    (let [account-numbers (.toString wtr)]
      (is (= (str "888888888 ERR\n490067715 ERR\n012345678 ERR\n"
                  "49006771? ILL\n12345678? ILL\n")
             account-numbers)))))

(deftest test-parse-digit
  (is (= "0" (parse-digit [" _ "
                           "| |"
                           "|_|"])))
  (is (= "1" (parse-digit ["   "
                           "  |"
                           "  |"])))
  (is (= "2" (parse-digit [" _ "
                           " _|"
                           "|_ "])))
  (is (= "3" (parse-digit [" _ "
                           " _|"
                           " _|"])))
  (is (= "4" (parse-digit ["   "
                           "|_|"
                           "  |"])))
  (is (= "5" (parse-digit [" _ "
                           "|_ "
                           " _|"])))
  (is (= "6" (parse-digit [" _ "
                           "|_ "
                           "|_|"])))
  (is (= "7" (parse-digit [" _ "
                           "  |"
                           "  |"])))
  (is (= "8" (parse-digit [" _ "
                           "|_|"
                           "|_|"])))
  (is (= "9" (parse-digit [" _ "
                           "|_|"
                           "  |"])))
  (is (= "?" (parse-digit ["   "
                           " _|"
                           "  |"])))
  (is (= "?" (parse-digit [" "
                           "_|"
                           "  |"])))
  (is (= nil (parse-digit ["" "" ""]))))
