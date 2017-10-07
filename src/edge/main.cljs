;; Copyright © 2016, JUXT LTD.

(ns edge.main
  (:require
   [reagent.core :as r]
   [edge.phonebook-app :as phonebook]
   [edge.clock :as clock]))

(defn init []
  (enable-console-print!)

  (when-let [section (. js/document (getElementById "clock"))]
    (println "Phonebook")
    (clock/init section))

  (println "Congratulations - your environment seems to be working"))
