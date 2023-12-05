(ns learning-with-soccer.core
  (:use [clojure pprint])
  (:require [clojure.string]
            [clojure.math :refer [round]]
            [learning-with-soccer.db :as db]
            [learning-with-soccer.models :as m]
            [learning-with-soccer.logic :as logic]
            [schema.core :as s]))

(s/defn get-team :- m/Team
  [id :- m/TeamId]
  (->> db/all-teams
       (filter #(= (:team-id %) id))
       first))

(s/defn match-str :- s/Str
  [match :- m/Match]
  (let [home-team  (:know-as (get-team (:home-team-id match)))
        home-goals (:goals-home match)
        away-goals (:goals-away match)
        away-team  (:know-as (get-team (:away-team-id match)))]
    (str home-team " " home-goals " x " away-goals " " away-team)))

;TODO consider odd numbers of teams, add a dummy matches and filter
;TODO consider two turns
(s/defn generate-tournament :- m/Tournament
  [teams :- [m/Team]]
  (let [matches (logic/round-robin teams)]
    matches))

; (println (logic/define-rounds (generate-tournament db/all-teams)))

(s/defn simulate-match! :- [m/Match]
  [match :- m/Match]
  (-> match
      (logic/set-goal-home (round (rand 4)))
      (logic/set-goal-away (round (rand 4)))))

(s/defn simulate-tournament!
  [matches :- [m/Match]]
  (map simulate-match! matches))

(def test-teams db/all-teams)

(def test-tournament {:matches (simulate-tournament! (generate-tournament test-teams)) :teams test-teams})

(print-table (reverse (sort-by :points (logic/tournament-table test-tournament))))

(defn tournament-summary
  [matches]
  (map match-str matches))

;(->> (generate-tournament db/all-teams)
;     (simulate-tournament!)
;     (tournament-summary)
;     (clojure.string/join "\n")
;     println)