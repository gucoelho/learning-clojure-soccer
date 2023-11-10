(ns learning-with-soccer.logic
  (:require  [clojure.math :refer [round]]
           [learning-with-soccer.core :as c]))

(defn set-goal-home
  ([match goal]
   (assoc match :goals-home goal))
  ([match]
   (set-goal-home match (-> match
                            (:goals-home 0)
                            inc))))

(defn set-goal-away
  ([match goal]
   (assoc match :goals-away goal))
  ([match]
   (set-goal-away match (-> match
                    :goals-away 0
                    inc))))

(defn simulate-match
  [match]
  (-> match
      (set-goal-home (round (rand 4)))
      (set-goal-away (round (rand 4)))))

(defn simulate-tournament
  [matches]
  (map simulate-match matches))
(->> (c/generate-tournament c/all-teams)
     (simulate-tournament)
     (c/tournament-summary)
     (clojure.string/join "\n")
     println)

