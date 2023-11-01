(ns learning-with-soccer.core)


(defn make-team
  "Create a team"
  [id, fullname, know-as, abbreviation] {
                                         :team-id      id
                                         :fullname     fullname
                                         :know-as      know-as
                                         :abbreviation abbreviation})


(def all-teams [(make-team (random-uuid) "Santos Futebol Clube" "Santos" "SAN")
                (make-team (random-uuid) "Palmeiras" "Palmeiras" "PAL")
                (make-team (random-uuid) "São Paulo" "São Paulo" "SPO" )
                (make-team (random-uuid) "Corinthians" "Corinthians" "COR" )
                (make-team (random-uuid) "Clube de Regatas Flamengo" "Flamengo" "FLA" )])


(defn get-team [id] (->> all-teams
                         (filter #(= (:team-id %) id))
                         (first)
                         :know-as))

(defn make-match
  "Create a match with a home team and an away team"
  [id home-team-id away-team-id]
  {
   :match-id  id
   :home-team home-team-id
   :away-team  away-team-id
   :status    :not-started })

(defn print-match
  [match]
  (println (get-team (:home-team match)) "x" (get-team (:away-team match)))
  match
  )

(defn round-robin
  "Create matches with all teams in :teams"
  [acc, team]
  (let [
        teams (filter #(not (= (:team-id team) (:team-id %))) (:teams acc))
        matches (:matches acc)
        new-matches (map #(make-match (random-uuid) (:team-id team) (:team-id %)) teams)
        ] {
           :matches (concat new-matches matches)
           :teams teams
           }))


(defn generate-tournament
  [teams]
  (let [round-count (- (count teams) 1)
        matches (:matches (reduce round-robin {:matches [] :teams teams} teams))]
    (println (map #(print-match %) matches))))

(generate-tournament all-teams)


(defn- set-goal-home
  [match goals] (assoc :goals-home goals match))

(defn- set-goal-away
  [match goals] (assoc :goals-away goals match))