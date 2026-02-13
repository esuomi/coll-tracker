(ns eu.esuomi.code.coll-tracker-test
  (:require [clojure.test :refer [deftest is testing]]
            [eu.esuomi.code.coll-tracker :as coll-tracker]))

(defn assert-access-produces
  [coll access expected-values expected-paths]
  (let [tracker (atom #{})
        tracked (coll-tracker/tracked coll tracker)]
    (is (= expected-values (access tracked)))
    (is (= expected-paths @tracker))))

(deftest collections-support
  (testing "map lookups"
    (let [data {:foo :bar :quu {:qux :etc}}]
      (assert-access-produces data #(:foo %) :bar #{[:foo]})
      (assert-access-produces data #(-> % :quu :qux) :etc #{[:quu] [:quu :qux]})))

  (testing "list lookups"
    (let [data (list "apples" "bananas" (list "milk" "cheese"))]
      (assert-access-produces data #(nth % 1) "bananas" #{[1]})
      (assert-access-produces data #(-> % (nth 2) (nth 1)) "cheese" #{[2] [2 1]})))

  (testing "vector lookups"
    (let [data [[:stick :branch :trunk] [[[[:deep]]]] [:fork :spoon :knife]]]
      (assert-access-produces data #(-> % (nth 0) (.value)) [:stick :branch :trunk] #{[0]})
      (assert-access-produces data #(-> % (nth 1) (nth 0) (nth 0) (nth 0) (nth 0)) :deep #{[1] [1 0] [1 0 0] [1 0 0 0] [1 0 0 0 0]})))

  (testing "set lookups"
    (let [data #{:clubs :diamonds :hearts :spades}]
      ; direct access is recorded as is
      (assert-access-produces data :diamonds :diamonds #{[:diamonds]})
      ; ambiguous access uses special value
      (assert-access-produces data #(do (let [_ (first %)] ::ok)) ::ok #{[:*]})))

  (testing "ambiguous set access"
    (let [data {:suits #{:clubs :diamonds :hearts :spades}
                :bags  #{{:type :backpack} {:type :handbag}}}]
      (assert-access-produces
        data
        (fn [coll]
          (-> coll :suits first)
          (-> coll :bags first :type)
          ::ok)
        ::ok
        #{[:bags] [:bags :*] [:suits] [:suits :*]}))))