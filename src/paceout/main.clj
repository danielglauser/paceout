(ns paceout.main
  "The entrypiont for the paceout account number parser. Handles
  command line arguments and the top level control flow."
  (:require [clojure.string :as string]
            [clojure.tools.cli :as cli])
  (:gen-class))

(def cli-options [["-h" "--help"]
                  ["-o" "--outfile"]])

(defn usage [options-summary]
  (->> [(str "parse: A tool for parsing ASCII account numbers.")
        ""
        "Usage: parse [options] filename"
        ""
        "Options:"
        options-summary
        ""
        "Where filename specifies a file containing account numbers of"
        "the following form:"
        ""
        "account-number"
        "blank line"
        "..."
        ""
        "Account numbers span three lines and look something like this:"
        " _  _  _  _  _  _  _  _  _ "
        "| || || || || || || || || |"
        "|_||_||_||_||_||_||_||_||_|"
        ""
        "For more information go here:"
        "https://github.com/danielglauser/paceout/blob/master/README.md"]
       (string/join "\n")))

(defn exit
  "Print an optional message and exit with the status."
  [status & [msg]]
  (when msg (println msg))
  (System/exit status))

(defn error-msg [errors]
  (str "The following errors occurred while parsing:\n\n"
       (string/join "\n" errors)))

(defn -main [& args]
  ;; options are parsed :in-order so we handle help first
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options :in-order true)]
    (cond
     (:help options) (exit 0 (usage summary))
     errors (exit 1 (error-msg errors))
     (= 0 (count arguments)) (exit 1 (usage summary)))
    (println "Did it.")))
