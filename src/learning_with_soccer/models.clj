(ns learning-with-soccer.models
  (:require [schema.core :as s]))

(s/defschema Team
  "Schema that represents a soccer team"
  {:id           s/Uuid
   :abbreviation s/Str
   :full-name    s/Str
   :know-as      s/Str})

(s/defschema Match
  "Schema that represents a soccer match"
  {:match-id                    s/Str
   :home-team-id                s/Uuid
   :away-team-id                s/Uuid
   (s/optional-key :goals-home) s/Int
   (s/optional-key :goals-away) s/Int
   (s/optional-key :round)      s/Int})

(s/defschema Tournament
  "Schema that represents a tournament"
  {:matches [Match]
   :teams   [Team]})

(s/defschema MatchesGroupedByRound {s/Int [Match]})