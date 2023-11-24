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
  {:match-id     s/Uuid
   :home-team-id s/Uuid
   :away-team-id s/Uuid
   :goals-home   s/Int
   :goals-away   s/Int
   :round        s/Int})

(s/defschema Tournament
  "Schema that represents a tournament"
  [Match])