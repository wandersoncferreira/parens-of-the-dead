(ns undead.client
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(def game {:board [{:face :h1} {:face :h1} {:face :h2} {:face :h2 :revealed? true}
                   {:face :h3} {:face :h3} {:face :h4} {:face :h4}
                   {:face :h5} {:face :h5} {:face :fg} {:face :fg}
                   {:face :zo} {:face :zo} {:face :zo} {:face :gy}]
           :sand (concat (repeat 20 :gone)
                         (repeat 30 :remaining))
           :foggy? false})

(defn cell
  [cell]
  [:div {:className "cell"}
   [:div {:className (str "tile"
                          (when (:revealed? cell) " revealed")
                          (when (:matched? cell) " revealed"))}
    [:div {:className "front"}]
    [:div {:className (str "back " (name (:face cell)))}]]])


(defn line
  [cells]
  [:div {:className "line"}
   (map cell cells)])


(defn board
  [cells]
  [:div {:className "board clearfix"}
   (map line (partition 4 cells))])

(defn timer
  [timer-parameters]
  (let [{:keys [sand index]} timer-parameters]
    [:div {:className (str "timer timer-" index)}
     (for [sand-kind sand]
       [:div {:className (str "sand " (name sand-kind))}])]))

(defn timers
  [sand]
  [:div
   (map-indexed #(timer {:index %1 :sand %2}) (partition 30 sand))])

(defn game-view
  []
  [:div {:className (when (:foggy? game) "foggy")}
   [board (:board game)]
   [timers (:sand game)]])


(defn ^:export ^:dev/after-load  init []
  (rdom/render [game-view] (js/document.getElementById "main")))
