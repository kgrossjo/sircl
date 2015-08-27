(ns sircl.util)

(defn map-hash
  "Map values to their results given function.
Given a function and a collection, apply the function
to each element of the collection, and return a map
of the elements of the collection to the corresponding
results of applying the function.
E.g. (map-hash f [1 2 3]) => {1 (f 1), 2 (f 2), 3 (f 3)}"
  [f coll]
  (reduce (fn [r x]
            (assoc r x (f x)))
          {}
          coll))
