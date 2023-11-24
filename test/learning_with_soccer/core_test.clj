(ns learning-with-soccer.core-test
  (:require [clojure.test :refer :all]
            [learning-with-soccer.core :refer :all]))

(deftest get-all-teams-from-matches-test
  (testing "test if fn returns a list of ids"
    (is (= (get-teams-from-matches [{
                                     :home-team-id 1
                                     :away-team-id 2
                                     }]) [1 2])))

  (testing "test if fn returns a list of ids and flat it"
    (is (= (get-teams-from-matches [{
                                     :home-team-id 1
                                     :away-team-id 2

                                     }
                                    {
                                     :home-team-id 2
                                     :away-team-id 3
                                     }
                                    ]) [1 2 3])))
  )

