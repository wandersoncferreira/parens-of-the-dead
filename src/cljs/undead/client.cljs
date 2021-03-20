(ns undead.client
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(def game {:tiles [{:face :h1} {:face :h1} {:face :h2} {:face :h2 :revealed? true}
                   {:face :h3} {:face :h3} {:face :h4} {:face :h4}
                   {:face :h5} {:face :h5} {:face :fg} {:face :fg}
                   {:face :zo} {:face :zo} {:face :zo} {:face :gy}]
           :sand (concat (repeat 20 :gone)
                         (repeat 30 :remaining))
           :foggy? false})

(defn cell
  [tile]
  [:div {:className "cell"}
   [:div {:className (str "tile"
                          (when (:revealed? tile) " revealed")
                          (when (:matched? tile) " revealed"))}
    [:div {:className "front"}]
    [:div {:className (str "back " (name (:face tile)))}]]])


(defn line
  [tiles]
  [:div {:className "line"}
   (map cell tiles)])


(defn board
  [tiles]
  [:div {:className "board clearfix"}
   (map line (partition 4 tiles))])

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
   [board (:tiles game)]
   [timers (:sand game)]])


(defn ^:export ^:dev/after-load  init []
  (rdom/render [game-view] (js/document.getElementById "main")))
