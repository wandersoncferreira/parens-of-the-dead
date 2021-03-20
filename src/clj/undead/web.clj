(ns undead.web
  (:require [chord.http-kit :as chord]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [undead.game-loop :refer [start-game-loop]]
            [clojure.java.io :as io]))

(defn- ws-handler
  [req]
  (chord/with-channel req ws-channel
    (start-game-loop ws-channel)))

(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/" [] (slurp (io/resource "public/index.html")))
  (resources "/"))
