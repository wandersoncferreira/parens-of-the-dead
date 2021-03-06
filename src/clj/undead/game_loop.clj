(ns undead.game-loop
  (:require [clojure.core.async :as a]
            [undead.game :refer [create-game prep reveal-tile tick]]))

(defn tick-every
  [ms]
  (let [c (a/chan)]
    (a/go-loop []
      (a/<! (a/timeout ms))
      (when (a/>! c :tick)
        (recur)))
    c))

(defn game-on?
  [game]
  (not (or (:safe? game) (:dead? game))))

(defn start-game-loop
  [ws-channel]
  (a/go
    (let [tick-ch (tick-every 200)]
     (loop [game (create-game)]
       (a/>! ws-channel (prep game))
       (if (game-on? game)
         (when-let [[value port] (a/alts! [ws-channel tick-ch])]
           (condp = port
             ws-channel (recur (reveal-tile game (:message value)))
             tick-ch (recur (tick game))))
         (do
           (a/close! tick-ch)
           (a/close! ws-channel)))))))
