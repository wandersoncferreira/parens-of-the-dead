(ns undead.components
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [cljs.core.async :refer [put!]]))

(defn cell
  [tile reveal-ch]
  [:div {:className "cell"}
   [:div {:className (str "tile"
                          (when (:revealed? tile) " revealed")
                          (when (:matched? tile) " revealed"))
          :onClick (fn [e]
                     (.preventDefault e)
                     (put! reveal-ch (:id tile)))}
    [:div {:className "front"}]
    [:div {:className (str "back " (when (:face tile)
                                     (name (:face tile))))}]]])


(defn line
  [tiles reveal-ch]
  [:div {:className "line"}
   (map #(cell % reveal-ch) tiles)])


(defn board
  [tiles reveal-ch]
  [:div {:className "board clearfix"}
   (map #(line % reveal-ch) (partition 4 tiles))])

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
  [game reveal-ch]
  [:div {:className (when (:foggy? game) "foggy")}
   [board (:tiles game) reveal-ch]
   [timers (:sand game)]])
