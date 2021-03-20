(ns undead.client
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [undead.components :as components]
            [chord.client :as chord]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def container (js/document.getElementById "main"))

(defn ^:export ^:dev/after-load init []
  (go
    (let [{:keys [ws-channel error]} (<! (chord/ws-ch "ws://localhost:9009/ws"))]
      (when error (throw error))
      (loop []
        (when-let [game (:message (<! ws-channel))]
          (rdom/render [components/game-view game ws-channel] container)
          (cond
            (:dead? game) (set! (.-className (.-body js/document)) "game-over")
            (:safe? game) (set! (.-location js/document) "/safe.html")
            :else (recur)))))))
