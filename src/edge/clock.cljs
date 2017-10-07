 (ns edge.clock
   (:require [reagent.core :as r]))

 (enable-console-print!)



 (def clock-state (r/atom {:time-atom (js/Date.)}))

 (defn tick []
   (swap! clock-state assoc :time-atom (js/Date.)))

 (defn clock []
   (let [time-now (:time-atom @clock-state)]
     [:div
      [:h1 (str time-now)]]))

 (.setInterval js/window tick 1000)

(defn init [section]
 (r/render-component [clock] section))

