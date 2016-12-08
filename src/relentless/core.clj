(ns relentless.core)

(defn- function-arity
  ""
  [f]
  (let [m (first (filter #(= "invoke" (.getName %)) (.getDeclaredMethods (class f))))
        p (.getParameterTypes m)]
    (alength p)))

(defn try-times*
  [thunk n handler]
  (loop [n n]
    (let [result (try
                   {:right (thunk)}
                   (catch Exception e
                     (if (zero? n)
                       (throw e)
                       {:left e})))]
      (if (contains? result :right)
        (:right result)
        (do
          (case (function-arity handler)
            0 (handler)
            1 (handler (:left result))
            2 (handler (:left result) n)
            (handler))
          (recur (dec n)))))))

(defmacro try-times
  "Try executing an expression.  If exceptions are raised, retry
   the expression `n' times.  In each failure case, a handler
   function will be called with the exception.

   The handler function can have arity of 0, 1 or 2 with the following
   arguments:

     0 - []
     1 - [exception]
     2 - [exception remaining-retries]


   Sample Usage:

   (try-times 3
              (fn [e] (println (str \"received exception: \" e)))
              (println \"running\")
              (/ 1 0))

   (try-times 3
              (fn [e n]
                (let [delay (nth [9000 4000 1000] (dec n))]
                  (println (str \"delaying for \" delay))
                  (Thread/sleep delay)))
              (println \"running\")
              (/ 1 0))
  "
  [n handler-fn & body]
  `(try-times* (fn [] ~@body) ~n ~handler-fn))
