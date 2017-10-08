(ns edge.pomodoro
  (:require [reagent.core :as r]))

(enable-console-print!)

(defonce interval (atom 0))

(def time-active (atom 0))

(def timer (r/atom 150000))

(def start-time (r/atom 25))

(def break-time (r/atom 5))

(def time-fn (atom dec))

(defn time-active? []
  (if (= @time-active 0)
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
  (display-time (seconds-to-time @timer))
  (swap! timer func))

(defn pause []
  (when (time-active?)
    (reset! time-active 0)
    (js/clearInterval @interval)))

(defn start []
  (reset! time-active 1)
  (reset! interval (js/setInterval #(keep-time @time-fn) 10)))

(defn reset []
  (let [starting (fn [n] ;; cap at 59 mins
                   (if (< n 60)
                     n
                     59))]
    (reset! time-active 0)
    (js/clearInterval @interval)
    (reset! timer (* 60 (* 100 (starting  @start-time))))))

(defn start-time-input []
  [:div.start-input
   "Set working timer (in minutes): "
   [:input {:type "text"
            :value @start-time
            :on-change #(reset! start-time (-> % .-target .-value))}]
   [:button {:on-click (fn [ev] (reset))} "Set!"]])

(defn break-time-input []
  [:div.break-input
   "Set break timer (in minutes): "
   [:input {:type "text"
            :value @break-time
            :on-change #(reset! break-time (-> % .-target .-value))}]
   [:button {:on-click (fn [ev] (reset))} "Set!"]])

(defn clock []
  [:div
   [start-time-input]
   [break-time-input]
   [:h1.clock  (display-time (seconds-to-time @timer))]
   [:button {:on-click (fn [ev] (start))} "Start!"]
   [:button {:on-click (fn [ev] (pause))} "Pause!"]
   [:button {:on-click (fn [ev] (reset))} "Reset!"]])



(defn init [section]
  
  (r/render-component [clock] section))


