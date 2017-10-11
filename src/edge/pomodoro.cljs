(ns edge.pomodoro
  (:require [reagent.core :as r]))

(enable-console-print!)

(defonce interval (atom 0))

(def time-active (r/atom 0)) ;doesnt work as boolean? not sure why..

(def start-timer (r/atom 150000))

(def break-timer (r/atom 1500))

(def start-time (r/atom 25))

(def break-time (r/atom 5))

(def time-fn (atom dec))

(def break-time? (r/atom 0))

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
  (if (zero? @break-timer)
    (do
      (reset)
      
      (start)))
  (if (zero? @start-timer)
    (do
      (reset! break-time? 1)
      (swap! break-timer func))
    (do
      (reset! break-time? 0)
      
      (swap! start-timer func))))

(defn pause []
  (when (time-active?)
    (reset! time-active 0)
    (js/clearInterval @interval)))

(defn start []
  (reset! time-active 1)
  (reset! interval (js/setInterval #(keep-time @time-fn) 10)))

(defn reset []
  (let [starting (fn [n] ;; cap input at 59 mins
                   (if (< n 60)
                     n
                     59))]
    (reset! break-time? 0)
    (reset! time-active 0)
    (js/clearInterval @interval)
    (reset! start-timer (* 60 (* 100 (starting  @start-time))))
    (reset! break-timer (* 60 (* 100 @break-time)))))

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



(defn work-clock []
  [:div
   [:h1 (when (time-active?)
          (if (zero? @break-time?)
            "Get to work!!"
            "Break time!!")) ]
   [start-time-input]
   [break-time-input]
   [:h1.work-clock  {:class (if (zero? @break-time?)
                              "show-work"                              
                              "hide"                             
                              )} (display-time (seconds-to-time @start-timer))]
   [:h1.break-clock {:class (if (zero? @break-time?)
                              "hide"
                              (if (< @break-timer 3000)
                                "show-break warning"
                                "show-break")
                              )} (display-time (seconds-to-time @break-timer))]
   [:button {:on-click (fn [ev] (start))} "Start!"]
   [:button {:on-click (fn [ev] (pause))} "Pause!"]
   [:button {:on-click (fn [ev] (reset))} "Reset!"]])



(defn init [section]
  
  (r/render-component [work-clock] section))


