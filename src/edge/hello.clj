
;; Copyright © 2016, JUXT LTD.
(ns edge.hello
  "Demonstrating a simple example of a yada web resource"
  (:require
   [yada.yada :as yada]
   [selmer.parser :as selmer]
   [clojure.java.io :as io]))
(defn clock-route []
  ["/clock"  (yada/resource
              {:id :edge.resources/clock
               :methods
               {:get
                {:produces #{"text/html"}
                 :response (fn [ctx] 
                             (selmer/render-file "clock.html" {:title "Clock"
                                                               :ctx ctx}))}}})])

(defn pomodoro-route []
 ["/pomodoro"  (yada/resource
             {:id :edge.resources/pomodoro
              :methods               {:get
               {:produces "text/html"
                :response (fn [ctx] 
                             (selmer/render-file "pomodoro.html" {:title "Pomodoro Timer"
                                                              :ctx ctx}))}}})])
(defn hello-language []
  ["/hello-language"
   (yada/resource
    {:methods
     {:get
      {:produces
       {:media-type "text/plain"
        :language #{"en" "zh-ch;q=0.9"}}
       :response
       #(case (yada/language %)
          "zh-ch" "你好世界\n"
          "en" "Hello bad World!\n")}}})])

(defn hello-atom []
  ["/hello-atom"
   (yada/as-resource (atom "Hello Bad World!\n"))])

(defn hello-parameter []
  ["/hello-parameter"
   (yada/resource
    {:methods
     {:get
      {:parameters {:query {:p String}}
       :produces "text/plain"
       :response (fn [ctx] (format "Hello %s!\n" (-> ctx :parameters :query :p)))
       }}})])

(defn other-hello-routes []
  ["" [
       (hello-language)
       (hello-atom)
       (hello-parameter)
       (pomodoro-route)
       ]])
