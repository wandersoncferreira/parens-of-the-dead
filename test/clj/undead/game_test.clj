(ns undead.game-test
  (:require [clojure.test :refer [deftest is testing]]
            [undead.game :as sut]))

(defn find-face-index
  [game face]
  (first (keep-indexed (fn [index tile]
                         (when (and (= face (:face tile))
                                    (not (:revealed? tile)))
                           index))
                       (:tiles game))))

(defn reveal-one
  [face game]
  (sut/reveal-tile game (find-face-index game face)))


;;; helpers

(deftest create-game-test
  (testing "create-game provide the correct number of tiles"
    (is (= {:h1 2 :h2 2 :h3 2 :h4 2 :h5 2
            :fg 2 :zo 3 :gy 1}
           (->> (sut/create-game)
                :tiles
                (map :face)
                frequencies))))

  (testing "create-game will produce a reasonable amount of different games when called."
    (is (< 10
           (count (set (repeatedly 100 sut/create-game))))))

  (testing "sand should be remaining 30 when started"
    (is (= {:remaining 30} (frequencies (:sand (sut/create-game)))))))


(deftest reveal-tile-test
  (testing "after reveal-tile we should see one tile revealed from new game"
    (is (= 1
           (->> (sut/reveal-tile (sut/create-game) 0)
                :tiles
                (filter :revealed?)
                count))))

  (testing "Only the revealed tiles should return back."
    (is (= [:h1 :h2]
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h2)
                :tiles
                (filter :revealed?)
                (map :face)
                sort))))

  (testing "Not possible to reveal 3 tiles in a row without turn them back first."
    (is (= [:h1 :h2]
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h2)
                (reveal-one :h3)
                :tiles
                (filter :revealed?)
                (map :face)
                sort))))

  (testing "Verify a matching pair"
    (is (= [:h1 :h1]
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h1)
                :tiles
                (filter :matched?)
                (map :face)))))

  (testing "After two tiles have been matched, we can revealed more right away."
    (is (= [:h3]
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h1)
                (reveal-one :h3)
                :tiles
                (filter :revealed?)
                (map :face))))))


(deftest board-turns-foggy-test
  (is (= true (->> (sut/create-game)
                   (reveal-one :fg)
                   (reveal-one :fg)
                   :foggy?))))

(deftest board-zoombies-test
  (testing "When you reveal two zombies you lose 3 sands"
    (is (= [:zombie :zombie :zombie :remaining]
           (->> (sut/create-game)
                (reveal-one :zo)
                (reveal-one :zo)
                :sand
                (take 4)))))

  (testing "Also when you reveal two zombies you should get more one zoombies in the board"
    (is (= {:h1 2 :h2 2 :h3 2 :h4 2 :h5 2
            :fg 2 :zo 4}
           (->> (sut/create-game)
                (reveal-one :zo)
                (reveal-one :zo)
                :tiles
                (map :face)
                frequencies)))))


(deftest prep-test
  (is (= {nil 16}
         (->> (sut/create-game)
              sut/prep
              :tiles
              (map :face)
              frequencies)))

  (testing "we want to keep revealed faces."
    (is (= {nil 15 :h1 1}
           (->> (sut/create-game)
                (reveal-one :h1)
                sut/prep
                :tiles
                (map :face)
                frequencies))))

  (testing "we want to keep matched faces."
    (is (= {nil 14 :h1 2}
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h1)
                sut/prep
                :tiles
                (map :face)
                frequencies)))))

(deftest create-game-with-ids-test
  (is (= (range 0 16)
         (->> (sut/create-game)
              sut/prep
              :tiles
              (map :id)))))

(deftest tick-concealment-test
  (testing "after two tick the number of revealed tiles should be still 2."
   (is (= 2
          (->> (sut/create-game)
               (reveal-one :h1)
               (reveal-one :h2)
               sut/tick
               sut/tick
               :tiles
               (filter :revealed?)
               count))))

  (testing "after three tick the number of revealed tiles should be 0"
   (is (= 0
          (->> (sut/create-game)
               (reveal-one :h1)
               (reveal-one :h2)
               sut/tick
               sut/tick
               sut/tick
               :tiles
               (filter :revealed?)
               count)))))

(deftest faces-must-be-for-while
  (is (= {nil 14, :h1 1 :h2 1}
         (->> (sut/create-game)
              (reveal-one :h1)
              (reveal-one :h2)
              sut/tick
              sut/tick
              sut/tick
              sut/prep
              :tiles
              (map :face)
              frequencies))))

(defn tick-n [n game]
  (first (drop n (iterate sut/tick game))))

(deftest tick-time-to-die-test
  (is (= [:gone :remaining]
        (->> (sut/create-game)
             (tick-n 5)
             :sand
             (take 2))))

  (testing "after 30 seconds all the sand must be gone."
    (is (= {:gone 30}
           (->> (sut/create-game)
                (tick-n 155)
                :sand
                frequencies))))

  (testing "after 31 seconds you are dead..."
    (is (= true
           (->> (sut/create-game)
                (tick-n 155)
                :dead?)))))

(defn reveal-two
  [face game]
  (->> game (reveal-one face) (reveal-one face)))

(defn reveal-all-houses
  [game]
  (->> game
       (reveal-two :h1)
       (reveal-two :h2)
       (reveal-two :h3)
       (reveal-two :h4)
       (reveal-two :h5)))

(deftest getting-out-alive-test
  (testing "after two ticks we havent closed the game yet"
    (is (not (->> (sut/create-game)
                  (reveal-all-houses)
                  sut/tick
                  sut/tick
                  :safe?)))))

(defn- complete-round
  [n game]
  (reduce
   (fn [game _]
     (->> game
          (reveal-all-houses)
          sut/tick sut/tick sut/tick))
   game
   (range n)))


(deftest complete-rounds-test
  (testing "after the first round we should have 60 sand"
    (is (= 60
           (->> (sut/create-game)
                (complete-round 1)
                :sand
                count))))

  (testing "and the board should be reset"
    (is (empty?
         (->> (sut/create-game)
              (complete-round 1)
              :tiles
              (filter :matched?)))))

  (testing "after two complete rounds there should be 90 sands"
    (is (= 90
           (->> (sut/create-game)
                (complete-round 2)
                :sand
                count))))

  (testing "after three rounds, you should be safe."
    (is (= true
           (->> (sut/create-game)
                (complete-round 3)
                :safe?)))))
