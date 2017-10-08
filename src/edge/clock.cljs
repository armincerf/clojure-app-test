 (ns edge.clock
   (:require [reagent.core :as r]))

 (enable-console-print!)

(def clock-state (r/atom {:current-time (js/Date.)}))

(def stopwatch-state (atom 0)) ;;0 represents pause, 1 start

(def clock-atom (atom 0))

(def start-time (atom 0))

(def time-fn (atom inc)) ;; inc for stopwatch, dec for countdown

(defonce interval (atom 0))



(defn stopwatch-started? [] 
  (if (= @stopwatch-state 0)
    false
    true))

(defn tick [] dosync
  (swap! clock-state assoc :current-time (js/Date.)))

(defn seconds-to-time [secs]
  (let [d (js/Date. (* 10 secs))]
    {:hours   (.getUTCHours d)
     :minutes (.getUTCMinutes d)
     :seconds (.getUTCSeconds d)
     :milliseconds (.getUTCMilliseconds d)}))

(defn display-time [tm]
  (let [pad (fn [n]
              (if (< n 10)
                (str "0" n)
                n))
        mm (pad (:minutes tm))
        ss (pad (:seconds tm))
        ms (pad (/ (:milliseconds tm) 10))]
    (str mm ":" ss ":" ms)))

(defn keep-time [func]
  (display-time (seconds-to-time @clock-atom))
  (swap! clock-atom func))

(defn pause []
  (when (stopwatch-started?)
    (swap! stopwatch-state dec)
    (js/clearInterval @interval)))

(defn start []
  (when-not (stopwatch-started?)
    (swap! stopwatch-state inc)
    (reset! interval (js/setInterval #(keep-time @time-fn) 10))))

(defn reset []
  (reset! stopwatch-state 0)
  (js/clearInterval @interval)
  (reset! clock-atom @start-time))

 (defn clock []
   (let [time-now (:current-time @clock-state)]
     [:div.clock
      [:h1 "Date = " (str time-now)]
      [:h1  "stopwatch = " (display-time (seconds-to-time @clock-atom))]
      [:button {:on-click (fn [ev] (start))} "Start!"]
      [:button {:on-click (fn [ev] (pause))} "Pause!"]
      [:button {:on-click (fn [ev] (reset))} "Reset!"]]))



(defn init [section]
  (.setInterval js/window tick 100)
  (r/render-component [clock] section))

