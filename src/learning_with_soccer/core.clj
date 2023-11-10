(ns learning-with-soccer.core
  (:use [clojure pprint])
  (:require [clojure.string]))

(defn make-team
  "Create a team"
  [id, abbreviation, full-name, know-as] {
                                          :team-id      id
                                          :abbreviation abbreviation
                                          :full-name    full-name
                                          :know-as      know-as})
(def all-teams [(make-team (random-uuid) "SAN" "Santos Futebol Clube" "Santos" )
                (make-team (random-uuid) "PAL" "Associação Esportiva Palmeiras" "Palmeiras")
                (make-team (random-uuid) "SPO" "São Paulo Futebol Clube" "São Paulo")
                (make-team (random-uuid) "COR" "Sport Club Corinthians Paulista" "Corinthians")
                (make-team (random-uuid) "RBR" "Red Bull Bragantino" "Bragantino")
                (make-team (random-uuid) "BOT" "Botafogo" "Botafogo")
                (make-team (random-uuid) "FLU" "Fluminense" "Fluminense")
                (make-team (random-uuid) "FLA" "Clube de Regatas Flamengo" "Flamengo")])
(defn get-team
  [id]
  (->> all-teams
       (filter #(= (:team-id %) id))
       (first)))
(defn make-match
  [id home-team-id away-team-id] {
                                  :match-id id
                                  :home-team-id home-team-id
                                  :away-team-id away-team-id
                                  :status :not-started})
(defn match-str
  [match]
  (let [home-team (:know-as (get-team (:home-team-id match)))
        home-goals (:goals-home match)
        away-goals (:goals-away match)
        away-team (:know-as (get-team (:away-team-id match)))]
    (clojure.string/join " " [home-team home-goals "x" away-goals away-team]) ))

(defn round-robin-with-reduce
  [matched-teams, team]
  (let [
        eligible-teams (filter #(not (= (:team-id team) (:team-id %) )) (:teams matched-teams))
        matches (:matches matched-teams)
        new-matches (map #(make-match (random-uuid) (:team-id team) (:team-id %)) eligible-teams)
        ] {
           :matches (concat new-matches matches)
           :teams eligible-teams
           }))

(defn round-robin-with-loop
  [teams]
    (loop [rest-teams teams
           matches []]
      (let [
            current-team (first rest-teams)
            eligible-teams  (filter #(not (= (:team-id current-team) (:team-id %))) rest-teams)
            new-matches (map #(make-match (random-uuid) (:team-id current-team) (:team-id %)) eligible-teams)]
        (if (seq eligible-teams)
          (recur eligible-teams (concat matches new-matches))
          matches)
        )))


(defn define-rounds
  [matches]
  (let [round-count 19]
    (loop [round 1
           defined-matches []
           rest-matches matches]
      (pprint round)
      (if (< round round-count)
        (recur (inc round) (assoc (first rest-matches) :round round) (rest rest-matches))
        defined-matches))))

(defn generate-tournament
  [teams]
  (let [matches (round-robin-with-loop teams)]
    matches))

(defn tournament-summary
  [matches]
  (map match-str matches))

(def tournament-str (clojure.string/join "\n" (tournament-summary (generate-tournament all-teams))))

(println tournament-str)
