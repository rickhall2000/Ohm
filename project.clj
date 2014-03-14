(defproject ohm "0.1.0"
  :description "Ohms law calculator demo written with Om"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.5.0"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "ohm"
              :source-paths ["src"]
              :compiler {
                :output-to "ohm.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
