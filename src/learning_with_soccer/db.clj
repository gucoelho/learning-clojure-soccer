(ns learning-with-soccer.db)

(defn make-team
  "Create a team"
  [id, abbreviation, full-name, know-as]
  {:team-id      id
   :abbreviation abbreviation
   :full-name    full-name
   :know-as      know-as})

(def all-teams [(make-team (random-uuid) "SAN" "Santos Futebol Clube" "Santos")
                (make-team (random-uuid) "PAL" "Associação Esportiva Palmeiras" "Palmeiras")
                (make-team (random-uuid) "SPO" "São Paulo Futebol Clube" "São Paulo")
                (make-team (random-uuid) "COR" "Sport Club Corinthians Paulista" "Corinthians")
                (make-team (random-uuid) "RBR" "Red Bull Bragantino" "Bragantino")
                (make-team (random-uuid) "VAS" "Club de Regatas Vasco da Gama" "VAS")
                (make-team (random-uuid) "INT" "Internacional" "Internacional")
                (make-team (random-uuid) "BOT" "Botafogo" "Botafogo")
                (make-team (random-uuid) "FLU" "Fluminense" "Fluminense")
                (make-team (random-uuid) "FLA" "Clube de Regatas Flamengo" "Flamengo")])
