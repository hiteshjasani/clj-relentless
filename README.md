# clj-relentless

A Clojure library designed to enable easy retrying of code in the face
of exceptions.

Networking code needs a retry strategy when things don't work the
first time.  Failures may be recoverable (temporary network glitches,
an overburdened server or an expired access token) or non-recoverable
and being able to influence subsequent retry attempts is valuable.


## Installation

tbd



## Usage

Calls that work the first time won't be retried.

```clojure
(try-times 3
           (fn [e] (println (str "received exception: " e)))
           (println "running")
           (+ 2 3))

;; running
;; 5
```

Example checking the exception.

```clojure
(try-times 3
           (fn [e] (println (str "received exception: " e)))
           (println "running")
           (/ 1 0))

;; running
;; received exception: java.lang.ArithmeticException: Divide by zero
;; running
;; received exception: java.lang.ArithmeticException: Divide by zero
;; running
;; received exception: java.lang.ArithmeticException: Divide by zero
;; running
;; ArithmeticException Divide by zero  clojure.lang.Numbers.divide (Numbers.java:158)
```

Example calling with an exponential backoff strategy.

```clojure
(try-times 4
           (fn [e n]
             (let [delay (nth [16000 9000 4000 1000] (dec n))]
               (println (str "delaying for " delay))
               (Thread/sleep delay)))
           (println "running")
           (/ 1 0))

;; running
;; delaying for 1000
;; running
;; delaying for 4000
;; running
;; delaying for 9000
;; running
;; delaying for 16000
;; running
;; ArithmeticException Divide by zero  clojure.lang.Numbers.divide (Numbers.java:158)
```

## License

Copyright © 2016 Hitesh Jasani

Distributed under the Eclipse Public License, the same as Clojure.
