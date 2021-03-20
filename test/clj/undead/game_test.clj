(ns undead.game-test
  (:require [undead.game :as sut]
            [clojure.test :refer [deftest is testing]]))

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
    (is (= #{{:face :h1 :revealed? true}
             {:face :h2 :revealed? true}}
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h2)
                :tiles
                (filter :revealed?)
                (set)))))

  (testing "Not possible to reveal 3 tiles in a row without turn them back first."
    (is (= #{{:face :h1 :revealed? true}
             {:face :h2 :revealed? true}}
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h2)
                (reveal-one :h3)
                :tiles
                (filter :revealed?)
                (set)))))

  (testing "Verify a matching pair"
    (is (= [{:face :h1 :matched? true}
            {:face :h1 :matched? true}]
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h1)
                :tiles
                (filter :matched?)))))

  (testing "After two tiles have been matched, we can revealed more right away."
    (is (= #{{:face :h3 :revealed? true}}
           (->> (sut/create-game)
                (reveal-one :h1)
                (reveal-one :h1)
                (reveal-one :h3)
                :tiles
                (filter :revealed?)
                (set))))))



