(ns learning-with-soccer.logic)

(defn make-match
  [id home-team-id away-team-id] {:match-id     id
                                  :home-team-id home-team-id
                                  :away-team-id away-team-id})

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


(defn generate-match-id
  [team1 team2]
  (str (:abbreviation team1) "x" (:abbreviation team2)))

(defn round-robin
  [teams]
  (loop [rest-teams teams
         matches    []]
    (let [current-team   (first rest-teams)
          eligible-teams (filter #(not (= (:team-id current-team) (:team-id %))) rest-teams)
          new-matches    (map #(make-match (generate-match-id current-team %) (:team-id current-team) (:team-id %)) eligible-teams)]
      (if (seq eligible-teams)
        (recur eligible-teams (concat matches new-matches))
        matches))))

