(ns user
  (:require [reloaded.repl :refer [system stop reset start go reset-all]]
            [undead.system]))

(reloaded.repl/set-init! #'undead.system/create-game)
