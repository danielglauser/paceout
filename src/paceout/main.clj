(ns paceout.main
  "The entrypiont for the paceout account number parser. Handles
  command line arguments and the top level control flow."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :as cli]
            [paceout.parser :as parser])
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

(defn get-reader [filename]
  (-> filename io/file io/reader))

(defn get-writer
  "Creates a writer to the optional filename. Defaults to stdout."
  [& [filename]]
  (if filename
    (-> filename io/file io/writer)
    (io/writer *out*)))

(defn -main [& args]
  ;; options are parsed :in-order so we handle help first
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options :in-order true)
        input-filename (first arguments)]
    (cond
     (:help options) (exit 0 (usage summary))
     errors (exit 1 (error-msg errors))
     (= 0 (count arguments)) (exit 1 (usage summary)))

    (when input-filename
          (with-open [in (get-reader input-filename)
                      out (get-writer)]
            (try
              (parser/parse-account-numbers in out)
              (catch Exception ex
                (println "Error when parsing: " (.getMessage ex))))))))
