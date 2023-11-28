(ns learning-with-soccer.logic
  (:require
   [learning-with-soccer.models :as m]
   [schema.core :as s]))

(s/defn make-match :- m/Match
  [id home-team-id away-team-id] {:match-id     id
                                  :home-team-id home-team-id
                                  :away-team-id away-team-id})

(s/defn set-goal-home :- m/Match
  ([match goal]
   (assoc match :goals-home goal))
  ([match]
   (set-goal-home match (-> match
                            :goals-home 0
                            inc))))

(s/defn set-goal-away :- m/Match
  ([match :- m/Match
    goal :- s/Int]
   (assoc match :goals-away goal))
  ([match]
   (set-goal-away match (-> match
                            :goals-away 0
                            inc))))

(defn generate-match-id
  [team1 team2]
  (str (:abbreviation team1) "x" (:abbreviation team2)))

(s/defn round-robin :- [m/Match]
  [teams :- [m/Team]]
  (loop [rest-teams teams
         matches    []]
    (let [current-team   (first rest-teams)
          eligible-teams (filter #(not (= (:team-id current-team) (:team-id %))) rest-teams)
          new-matches    (map #(make-match (generate-match-id current-team %) (:team-id current-team) (:team-id %)) eligible-teams)]
      (if (seq eligible-teams)
        (recur eligible-teams (concat matches new-matches))
        matches))))

(s/defn get-teams-from-matches :- [m/Team]
  [matches :- [m/Match]]
  (->> matches
       (map #(vals (select-keys % [:home-team-id :away-team-id])))
       flatten
       distinct
       set))

(s/defn round-contains-teams? :- s/Bool
  [team-home
   team-away
   teams-in-round]
  (or (contains? teams-in-round team-home)
      (contains? teams-in-round team-away)))

(s/defn add-team-to-round
  [match matches round]
  (let [match-with-round (assoc match :round round)
        new-round        (conj (get matches round) match-with-round)]
    (assoc matches round new-round)))

(s/defn next-round-available :- s/Int
  [match defined-rounds]
  (loop [round 1]
    (let [round-matches  (get defined-rounds round)
          teams-in-round (get-teams-from-matches round-matches)]
      (if (round-contains-teams? (:home-team-id match) (:away-team-id match) teams-in-round)
        (recur (inc round))
        round))))

(s/defn define-rounds :- m/MatchesGroupedByRound
  [matches :- m/Match]
  (loop [defined-matches {}
         rest-matches    matches]
    (if (not-empty rest-matches)
      (let [current-match    (first rest-matches)
            other-matches    (rest rest-matches)
            match-round      (next-round-available current-match defined-matches)
            round-with-match (add-team-to-round current-match defined-matches match-round)]
        (recur round-with-match other-matches))
      defined-matches)))

(s/defn team-match? :- s/Bool
  [team :- m/Team
   match :- m/Match]
  (or (= (:team-id team) (:home-team-id match))
      (= (:team-id team) (:away-team-id match))))

(s/defn filter-by-team :- [m/Match]
  [team :- m/Team
   matches :- [m/Match]]
  (filter #(team-match? team %) matches))

(s/defn winner
  [match :- m/Match]
  (let [home (:home-team-id match)
        away (:away-team-id match)]
    (if (> (:goals-home match) (:goals-away match)) home
                                                    (if (< (:goals-home match) (:goals-away match)) away
                                                                                                    nil))))
