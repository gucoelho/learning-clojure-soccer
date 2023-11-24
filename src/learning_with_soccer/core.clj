(ns learning-with-soccer.core
  (:use [clojure pprint])
  (:require [clojure.string]
            [clojure.math :refer [round]]
            [learning-with-soccer.db :as db]
            [learning-with-soccer.logic :as logic]
            ))

(defn get-team
  [id]
  (->> db/all-teams
       (filter #(= (:team-id %) id))
       (first)))

(defn match-str
  [match]
  (let [home-team  (:know-as (get-team (:home-team-id match)))
        home-goals (:goals-home match)
        away-goals (:goals-away match)
        away-team  (:know-as (get-team (:away-team-id match)))]
    (clojure.string/join " " [home-team home-goals "x" away-goals away-team])))

(defn get-teams-from-matches
  [matches]
  (->> matches
       (map #(vals (select-keys % [:home-team-id :away-team-id])))
       flatten
       distinct
       set))

(defn time-esta-nessa-rodada? [team-home team-away teams-in-round]
  (or (contains? teams-in-round team-home)
      (contains? teams-in-round team-away)))

(defn adiciona-time-na-rodada
  [match matches round]
  (let [match-with-round (assoc match :round round)
        new-round        (conj (get matches round) match-with-round)]
    (assoc matches round new-round)))

(defn proxima-rodada-que-nenhum-dos-dois-times-estao
  [match defined-rounds]
  (loop [round 1]
    (let [round-matches  (get defined-rounds round)
          teams-in-round (get-teams-from-matches round-matches)]
      (if (time-esta-nessa-rodada? (:home-team-id match) (:away-team-id match) teams-in-round)
        (recur (inc round))
        round))))

(defn define-rounds
  [matches]
  (loop [
         defined-matches {}
         rest-matches    matches]
    (if (not-empty rest-matches)
      (let [current-match    (first rest-matches)
            other-matches    (rest rest-matches)
            match-round      (proxima-rodada-que-nenhum-dos-dois-times-estao current-match defined-matches)
            round-with-match (adiciona-time-na-rodada current-match defined-matches match-round)]
        (recur round-with-match other-matches))
      defined-matches)))

(defn generate-tournament
  [teams]
  (let [matches (logic/round-robin teams)]
    matches))

(println (define-rounds (generate-tournament db/all-teams)))
(defn simulate-match!
  [match]
  (-> match
      (logic/set-goal-home (round (rand 4)))
      (logic/set-goal-away (round (rand 4)))))

(defn simulate-tournament!
  [matches]
  (map simulate-match! matches))

(defn tournament-summary
  [matches]
  (map match-str matches))

;
;(->> (generate-tournament db/all-teams)
;     (simulate-tournament!)
;     (tournament-summary)
;     (clojure.string/join "\n")
;     println)
