(ns edge.pomodoro
  (:require [reagent.core :as r]))

(enable-console-print!)

(defonce interval (atom 0))

(def time-active? (r/atom false))

(def work-timer (r/atom 150000))

(def break-timer (r/atom 1500))

(def work-time (r/atom 25))

(def break-time (r/atom 5))

(def time-fn (atom dec))

(def break-time? (r/atom false))



(defn seconds-to-time [secs]
  (let [d (js/Date. (* 10 secs))]
    {:hours   (.getUTCHours d)
     :minutes (.getUTCMinutes d)
     :seconds (.getUTCSeconds d)
     :milliseconds (.getUTCMilliseconds d)}))

(defn display-time [tm]
"return str of mins secs and millisecs with padding"
  (let [pad (fn [n]
              (if (< n 10)
                (str "0" n)
                n))
        mm (pad (:minutes tm))
        ss (pad (:seconds tm))
        ms (pad (/ (:milliseconds tm) 10))]
    (str mm ":" ss ":" ms)))

(defn keep-time [func]
"Starts appropriate timer (break or work) and checks if timer is 0"
  (if (zero? @break-timer)
    (do
      (reset)     
      (start))
    (if (zero? @work-timer)
      (do
        (reset! break-time? true)
        (swap! break-timer func))
      (do
        (reset! break-time? false)
        (swap! work-timer func)))))

(defn pause []
"if timer is running stop it"
  (when @time-active?
    (reset! time-active? true)
    (js/clearInterval @interval)))

(defn start []
"if no timer is running, start it"
  (reset! time-active? true)
  (reset! interval (js/setInterval #(keep-time @time-fn) 10)))

(defn reset [] ;todo - reset only break timer when in break time
"set the work and break timers and stop any running"
  (let [starting (fn [n] ;; cap input at 59 mins
                   (if (< n 60)
                     n
                     59))]
    (reset! break-time? false)
    (reset! time-active? false)
    (js/clearInterval @interval)
    (reset! work-timer (* 60 (* 100 (starting  @work-time))))
    (reset! break-timer (* 60 (* 100 @break-time)))))

(defn start-time-input []
  [:div.start-input
   "Set working timer (in minutes): "
   [:input {:type "text"
            :value @work-time
            :on-change #(reset! work-time (-> % .-target .-value))}]
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
   [:h1 (when @time-active?
          (if @break-time?            
            "Break time!!"
            "Get to work!!")) ]
   [start-time-input]
   [break-time-input]
   [:h1.work-clock  {:class (if @break-time?
                              "hide"
                              "show-work"                              
                              )} (display-time (seconds-to-time @work-timer))]
   [:h1.break-clock {:class (if @break-time?                              
                              (if (< @break-timer 3000)
                                "show-break warning"
                                "show-break")
                              "hide"
                              )} (display-time (seconds-to-time @break-timer))]
   [:button {:on-click (fn [ev] (start))} "Start!"]
   [:button {:on-click (fn [ev] (pause))} "Pause!"]
   [:button {:on-click (fn [ev] (reset))} "Reset!"]])



(defn init [section]
  
  (r/render-component [work-clock] section))


