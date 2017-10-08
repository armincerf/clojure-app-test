 (ns edge.clock
   (:require [reagent.core :as r]))

 (enable-console-print!)



(def clock-state (r/atom {:current-time (js/Date.)}))

(def stopwatch-state (atom 0)) ;0 represents pause, 1 start

(defn tick [] dosync
  (swap! clock-state assoc :current-time (js/Date.)))

(defn seconds-to-time
  [secs]
  (let [d (js/Date. (* secs 1000))]
    {:hours   (.getUTCHours d)
     :minutes (.getUTCMinutes d)
     :seconds (.getUTCSeconds d)}))

(defn display-time
  "Pretty-prints minutes:seconds, given time map of the form {:hours x :seconds y}."
  [tm]
  (let [pad (fn [n]
              (if (< n 10)
                (str "0" n)
                n))
        mm (pad (:minutes tm))
        ss (pad (:seconds tm))]
    (str mm ":" ss)))

(def clock-atom (atom 0))
(def start-time (atom 0))

(def time-fn (atom inc)) ;; included so we can swap it out for countdowns

(defn keep-time [func]
  (display-time (seconds-to-time @clock-atom))
  (swap! clock-atom func))

(defonce interval (atom 0))

(defn pause []
  (if (= @stopwatch-state 1)
    (do
      (swap! stopwatch-state dec)
      (js/clearInterval @interval))))

(defn start []
  (if (= @stopwatch-state 0)
    (do
      (swap! stopwatch-state inc)
      (reset! interval (js/setInterval #(keep-time @time-fn) 1000)))))

(defn reset []
  (reset! stopwatch-state 0)
  (js/clearInterval @interval)
  (reset! clock-atom @start-time))






 (defn clock []
   (let [time-now (:current-time @clock-state)
         val (r/atom 5)]
     [:div.clock
      [:h1 "time = " (str time-now)]
      [:h1  "stopwatch = " (display-time (seconds-to-time @clock-atom))]
      [:h3 "Stopwatch state = " (str @stopwatch-state)]
      [:button {:on-click (fn [ev] (start))} "Start!"]
      [:button {:on-click (fn [ev] (pause))} "Pause!"]
      [:button {:on-click (fn [ev] (reset))} "Reset!"]]))

(.setInterval js/window tick 500)

(defn init [section]
 (r/render-component [clock] section))

