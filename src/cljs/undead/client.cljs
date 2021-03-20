(ns undead.client
  (:require [chord.client :as chord]
            [cljs.core.async :refer [<!]]
            [reagent.dom :as rdom]
            [undead.components :as components])
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
