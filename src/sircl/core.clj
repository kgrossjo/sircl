(ns sircl.core
  (:require [sircl.index :as index])
  (:require [sircl.search :as search])
  (:gen-class))

(defn index-collection
  "Create an index on disk."
  [index directory]
  (printf "Indexing directory %s writing to file %s\n"
          directory index)
  (index/index-directory directory index))

(defn search-collection
  "Search an on-disk index by some terms."
  [index & terms]
  (printf "Searching in index file %s\n" index)
  (let [r (apply search/search index terms)]
    (doall (map (fn [x] (printf "%f\t%s\n" (float (second x)) (first x))) r))
    (println "All done.")))

(defn -main
  "Index a collection of documents or search in an index."
  [& args]
  (println "SIRCL here.")
  (let [cmd (first args)]
    (cond
      (= cmd "index")
      (apply index-collection (rest args))
      (= cmd "search")
      (apply search-collection (rest args)))))
