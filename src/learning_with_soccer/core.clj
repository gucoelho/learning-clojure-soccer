(ns testproject.core
  (:require [clojure.string]))

(defn make-team
  "Create a team"
  [id, abbreviation, full-name, know-as] {:team-id      id
                                          :abbreviation abbreviation
                                          :full-name    full-name
                                          :know-as      know-as})


(def all-teams [(make-team (random-uuid) "SAN" "Santos Futebol Clube" "Santos")
                (make-team (random-uuid) "PAL" "Palmeiras" "Palmeiras")
                (make-team (random-uuid) "SPO" "São Paulo" "São Paulo")
                (make-team (random-uuid) "COR" "Corinthians" "Corinthians")
                (make-team (random-uuid) "FLA" "Clube de Regatas Flamengo" "Flamengo")])

(defn get-team
  [id]
  (->> all-teams
       (filter #(= (:team-id %) id))
       (first)))

(defn make-match
  [id home-team-id away-team-id] {:match-id  id
                                  :home-team-id home-team-id
                                  :away-team-id  away-team-id
                                  :status    :not-started})

(defn match-str
  [match]
  (let [home-team (:know-as (get-team (:home-team-id match)))
        away-team (:know-as (get-team (:away-team-id match)))]
    (clojure.string/join " " [home-team "x" away-team])))

(defn round-robin
  [matched-teams, team]
  (let [eligible-teams (filter #(not (= (:team-id team) (:team-id %))) (:teams matched-teams))
        matches (:matches matched-teams)
        new-matches (map #(make-match (random-uuid) (:team-id team) (:team-id %)) eligible-teams)] {:matches (concat new-matches matches)
                                                                                                    :teams eligible-teams}))

(defn generate-tournament
  [teams]
  (let [reduced (reduce round-robin {:matches [] :teams teams} teams)
        matches (:matches reduced)]
    (map #(match-str %) matches)))

(def tournament-str (clojure.string/join "\n" (generate-tournament all-teams)))
(println tournament-str)