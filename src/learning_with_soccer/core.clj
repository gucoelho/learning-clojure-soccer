(ns learning-with-soccer.core
  (:use [clojure pprint])
  (:require [clojure.string]
            [clojure.math :refer [round]]
            [learning-with-soccer.db :as db]
            [learning-with-soccer.models :as m]
            [learning-with-soccer.logic :as logic]
            [schema.core :as s]))

(defn get-team
  [id]
  (->> db/all-teams
       (filter #(= (:team-id %) id))
       (first)))

(s/defn match-str :- s/Str
  [match :- m/Match]
  (let [home-team  (:know-as (get-team (:home-team-id match)))
        home-goals (:goals-home match)
        away-goals (:goals-away match)
        away-team  (:know-as (get-team (:away-team-id match)))]
    (str home-team " " home-goals " x " away-goals " " away-team)))

(s/defn generate-tournament :- m/Tournament
  [teams :- [m/Team]]
  (let [matches (logic/round-robin teams)]
    matches))

; (println (logic/define-rounds (generate-tournament db/all-teams)))

(defn simulate-match!
  [match]
  (-> match
      (logic/set-goal-home (round (rand 4)))
      (logic/set-goal-away (round (rand 4)))))

(defn simulate-tournament!
  [matches]
  (map simulate-match! matches))


(def test-teams db/all-teams)

(def t {:matches (simulate-tournament! (generate-tournament test-teams)) :teams test-teams})

(defn count-points [team acc match]
  (let [winner-id (logic/winner match)]
    (if (= nil winner-id)
      (inc acc)
      (if (= (:team-id team) winner-id)
        (+ 3 acc)
        acc))))

(defn count-victories [team acc match]
  (let [winner-id (logic/winner match)]
    (if (= (:team-id team) winner-id)
      (inc acc)
      acc)))

(defn count-draws [acc match]
  (let [winner-id (logic/winner match)]
    (if (= nil winner-id)
      (inc acc)
      acc)))

(s/defn tournament-table
  [tournament]
  (let [matches (:matches tournament)
        teams   (:teams tournament)]
    (loop [
           rest-teams (rest teams)
           table      []]
      (if-not (empty? rest-teams)
        (let [team         (first rest-teams)
              team-matches (logic/filter-by-team team matches)
              points       (reduce #(count-points team %1 %2) 0 team-matches)
              victories    (reduce #(count-victories team %1 %2) 0 team-matches)
              draws        (reduce #(count-draws %1 %2) 0 team-matches)
              looses       (- (count team-matches) (+ victories draws))]
          (recur (rest rest-teams) (conj table {:team      (:know-as team)
                                                :points    points
                                                :victories victories
                                                :draws     draws
                                                :looses    looses})))
        table))))

(print-table (reverse (sort-by :points (tournament-table t))))

(defn tournament-summary
  [matches]
  (map match-str matches))

;(->> (generate-tournament db/all-teams)
;     (simulate-tournament!)
;     (tournament-summary)
;     (clojure.string/join "\n")
;     println)
