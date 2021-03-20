(ns undead.system
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as server]
            [undead.web :refer [app]]))

(defn- start-server
  [handler port]
  (let [server (server/run-server handler {:port port})]
    (println (str "Started server on localhost:" port))
    server))

(defn- stop-server
  [server]
  (when server
    (server)))

(defrecord ParensOfTheDead []
  component/Lifecycle
  (start [this]
    (assoc this :server (start-server #'app 9009)))
  (stop [this]
    (stop-server (:server this))
    (dissoc this :server)))

(defn create-system
  []
  (ParensOfTheDead.))


(defn -main
  [& args]
  (create-system))
