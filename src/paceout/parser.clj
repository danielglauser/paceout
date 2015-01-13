(ns paceout.parser
  "The parsing libraries for the paceout account parser. Expects a
  file of account numbers and an optional file for recording output."
  (:require [clojure.string :as string]))

(defn checksum
  "Given a nine digit account number represented as a string returns
  the value of the following checksum: ((1*d1) + (2*d2) + (3*d3) + ...
  + (9*d9)) mod 11 == 0"
  [str-num]
  (if (re-find #"\?" str-num)
    false
    (let [multiplied (map-indexed (fn [idx char]
                                    (* (+ 1 idx)
                                       (-> char str Integer/parseInt)))
                                  (reverse str-num))
          added (reduce + multiplied)]
      (mod added 11))))

(defn read-three-lines!
  "Returns a vector of three strings corresponding to the next three
  lines from the reader."
  [#^java.io.Reader reader]
  [(.readLine reader) (.readLine reader) (.readLine reader)])

(defn read-blank-line!
  "Reads a single line from the reader, complains if the line isn't
  blank. If a non-blank line is encountered the reader is a
  LineNumberReader the line number will be part of the exception."
  [#^java.io.Reader reader]
  (let [line (.readLine reader)]
    (when-not (string/blank? line)
      (throw
       (Exception.
        (str "Expected blank line"
             (when (instance? java.io.LineNumberReader reader)
               (str " at line number: " (.getLineNumber reader)))))))))

(defn safe-subs
  "Returns the requested substring or nil if there isn't enough string
  left."
  [string start & [end]]
  (try
    (when string
      (if-not end
        (subs string start)
        (subs string start end)))
    (catch StringIndexOutOfBoundsException ex
      nil)))

(defn parse-column
  "Given a seq of strings returns a seq of num characters as the first
  element in a vector and as seq of the rest of the characters. For
  example, (parse-column 1 [\"abc\" \"def\"]) would return:
  [(\"a\" \"d\") (\"bc\" \"ef\")]."
  [num seq-of-strings]
  (if-not (empty? seq-of-strings)
    [(for [string seq-of-strings] (safe-subs string 0 num))
     (for [string seq-of-strings] (safe-subs string num))]
    nil))

(declare parse-digit)

(defn parse-account-number*
  "Return a string representation of a nine digit account number.
  Unrecognized characters show up as question marks."
  [three-lines]
  (loop [[ascii-digit remaining] (parse-column 3 three-lines)
         digits []]
    (let [digit (parse-digit ascii-digit)]
      (if (string/blank? digit)
        (string/join digits)
        (recur (parse-column 3 remaining)
               (conj digits digit))))))

(defn parse-account-number
  "Reads the next four lines from the reader, returning the first
  three."
  [reader]
  (let [three-lines (read-three-lines! reader)
        _ (read-blank-line! reader)]
    (parse-account-number* three-lines)))

(defn get-parsed-status
  "Given an account number returns nil for valid account numbers,
  \"ILL\" if the account number contains unparsed digits and \"ERR\"
  if it is not valid."
  [account-number valid?]
  (cond
   (re-find #"\?" account-number) "ILL"
   (and valid?
        (not (valid? account-number))) "ERR"
   :default nil))

(defn parse-account-numbers
  "Parses account numbers from the reader and writes them to the writer.
  Skips illegal account numbers that aren't valid?."
  [reader writer & [valid? include-output-status]]
  (loop [account-number (parse-account-number reader)]
    (when-not (string/blank? account-number)
      (let [status (get-parsed-status account-number valid?)
            formatted-status (if status
                               (str " " status)
                               status)]
        (.write writer
                (str account-number
                     (when include-output-status formatted-status)
                     "\n"))
        (recur (parse-account-number reader))))))

(defn parse-digit
  "Reads a seq of three strings of three characters, underscores, pipes,
  and spaces and translates them into a digit 0-9. If the digit is not
  recognized then a question mark is returned."
  [ascii-digit]
  (condp = ascii-digit
    [" _ "
     "| |"
     "|_|"] "0"
    ["   "
     "  |"
     "  |"] "1"
    [" _ "
     " _|"
     "|_ "] "2"
    [" _ "
     " _|"
     " _|"] "3"
    ["   "
     "|_|"
     "  |"] "4"
    [" _ "
     "|_ "
     " _|"] "5"
    [" _ "
     "|_ "
     "|_|"] "6"
    [" _ "
     "  |"
     "  |"] "7"
    [" _ "
     "|_|"
     "|_|"] "8"
    [" _ "
     "|_|"
     " _|"] "9"
    ["" "" ""] nil
    [nil nil nil] nil
    nil nil
    "?"))
