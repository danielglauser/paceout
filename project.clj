(defproject paceout "0.1.0-SNAPSHOT"
  :description "Command line utilitu for parsing ASCII account numbers."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main paceout.main
  :aot [paceout.main])
