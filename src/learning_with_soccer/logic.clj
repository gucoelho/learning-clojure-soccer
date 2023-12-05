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
  [team-home :- m/TeamId
   team-away :- m/TeamId
   teams-in-round :- [m/TeamId]]
  (or (contains? teams-in-round team-home)
      (contains? teams-in-round team-away)))

(s/defn add-team-to-round :- [m/Match]
  [match :- m/Match
   matches :- [m/Match]
   round :- m/MatchesGroupedByRound]
  (let [match-with-round (assoc match :round round)
        new-round        (conj (get matches round) match-with-round)]
    (assoc matches round new-round)))

(s/defn next-round-available :- s/Int
  [match :- m/Match
   defined-rounds :- [m/MatchesGroupedByRound]]
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

(s/defn winner :- (s/maybe m/TeamId)
  [match :- m/Match]
  (let [home (:home-team-id match)
        away (:away-team-id match)]
    (cond (> (:goals-home match) (:goals-away match)) home
          (< (:goals-home match) (:goals-away match)) away
          :else nil)))

(s/defn count-points :- s/Int
  [team :- m/Team
   points :- s/Int
   match :- m/Match]
  (let [winner-id (winner match)]
    (cond (= nil winner-id) (inc points)
          (= (:team-id team) winner-id) (+ 3 points)
          :else points)))

(s/defn count-victories :- s/Int
  [team :- m/Team
   victories :- s/Int
   match :- m/Match]
  (let [winner-id (winner match)]
    (if (= (:team-id team) winner-id)
      (inc victories)
      victories)))

(s/defn count-draws :- s/Int
  [draws :- s/Int
   match :- m/Match]
  (let [winner-id (winner match)]
    (if (= nil winner-id)
      (inc draws)
      draws)))

(s/defn tournament-table :- [m/TournamentTableRow]
  [tournament :- m/Tournament]
  (let [matches (:matches tournament)
        teams   (:teams tournament)]
    (loop [rest-teams (rest teams)
           table      []]
      (if-not (empty? rest-teams)
        (let [team         (first rest-teams)
              team-matches (filter-by-team team matches)
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

