(ns relentless.core-test
  (:require [clojure.test :refer :all]
            [relentless.core :refer :all]))

(deftest retries-multiple-times
  (testing "works first time"
    (let [ctr (atom 0)]
      (is (= 5 (try-times 3
                          (fn [e] (swap! ctr inc))
                          (+ 2 3))))
      (is (zero? @ctr))))

  (testing "retry 3 times before succeeding"
    (let [ctr (atom 0)
          denom (atom 0)]
      (is (= 2 (try-times 3
                          (fn [e n]
                            (swap! ctr inc)
                            ;; (println (str "n = " n))
                            (when (and (instance? java.lang.ArithmeticException e)
                                     (= n 1))
                              (reset! denom 5)))
                          (/ 10 @denom)
                          )))
      (is (= 3 @ctr))))
  )
