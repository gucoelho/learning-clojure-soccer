(ns learning-with-soccer.models
  (:require [schema.core :as s]))

(s/def TeamId s/Uuid)
(s/def MatchId s/Uuid)

(s/defschema Team
  "Schema that represents a soccer team"
  {:id           TeamId
   :abbreviation s/Str
   :full-name    s/Str
   :know-as      s/Str})

(s/defschema Match
  "Schema that represents a soccer match"
  {:match-id                    MatchId
   :home-team-id                TeamId
   :away-team-id                TeamId
   (s/optional-key :goals-home) s/Int
   (s/optional-key :goals-away) s/Int
   (s/optional-key :round)      s/Int})

(s/defschema Tournament
  "Schema that represents a tournament"
  {:matches [Match]
   :teams   [Team]})

(s/defschema MatchesGroupedByRound {s/Int [Match]})

(s/defschema TournamentTableRow
  {:team      s/Str
   :points    s/Int
   :victories s/Int
   :draws     s/Int
   :looses    s/Int})