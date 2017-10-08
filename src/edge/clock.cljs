 (ns edge.clock
   (:require [reagent.core :as r]))

 (enable-console-print!)

(defonce time-atom (r/atom (js/Date.)))

(defonce time-updater (js/setInterval
                       #(reset! time-atom (js/Date.)) 1000))

(defonce time-colour (r/atom "#16a"))

(def stopwatch-state (atom 0)) ;;0 represents pause, 1 start

(def clock-atom (r/atom 0))

(def start-time (atom 0))

(def time-fn (atom inc)) ;; inc for stopwatch, dec for countdown

(defonce interval (atom 0))

(defn colour-input []
  [:div.colour-input
   "Time colour: "
   [:input {:type "text"
            :value @time-colour
            :on-change #(reset! time-colour (-> % .-target .-value))}]])

(defn stopwatch-started? [] 
  (if (= @stopwatch-state 0)
    false
    true))

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

(defn timefn []
  (let [time-str (-> @time-atom .toTimeString (clojure.string/split " ") first)]
    [:div.clock
     {:style {:color @time-colour}}
     time-str]))

 (defn clock []
   [:div
    [:h1 "Current Time is:" [timefn]]
    [colour-input]
    [:h1.clock  (display-time (seconds-to-time @clock-atom))]
    [:button {:on-click (fn [ev] (start))} "Start!"]
    [:button {:on-click (fn [ev] (pause))} "Pause!"]
    [:button {:on-click (fn [ev] (reset))} "Reset!"]])



(defn init [section]
  
  (r/render-component [clock] section))

