(ns sircl.search
  (:require [clojure.java.io :as io]))

(defn read-index
  "Read the index structure from disk."
  [file-name]
  (with-open [r (io/reader file-name)]
    (read (java.io.PushbackReader. r))))

(defn get-inverted-list
  "Given an index and a term, get the inverted list for that term."
  [idx term]
  (get (:index idx) term))

(defn get-document-frequency
  "Given an index and a term, get the document frequency of that term."
  [idx term]
  (let [c (:collection idx)
        v (:vocabulary c)]
    (get v term)))

(defn get-result-for-term
  "Given an index and a term, compute the document rankings for that term."
  [idx term]
  (let [inverted-list (get-inverted-list idx term)
        doc-freq (get-document-frequency idx term)]
    (apply hash-map (mapcat (fn [x] [(first x) (/ (second x) doc-freq)])
                            inverted-list))))

(defn get-results-for-terms
  "Given an index and some terms, compute the document rankings for all the terms."
  [idx & terms]
  (apply hash-map (mapcat (fn [x] [x (get-result-for-term idx x)]) terms)))

(defn get-documents-for-terms
  "Given document rankings for some terms, compute the list of documents."
  [term-results]
  (apply hash-set (mapcat keys (vals term-results))))

(defn get-document-ranking
  "Given term results, get the overall ranking for a document."
  [term-results file-name]
  ;; This is not the right formula, but that's not the point.
  (apply + (map (fn [x] (get x file-name 0)) (vals term-results))))

(defn rank-results
  "Given rankings for some terms, compute the overall document ranking."
  [term-results]
  (let [docs (get-documents-for-terms term-results)]
    (map (fn [x] [x (get-document-ranking term-results x)]) docs)))
  
(defn search-index
  "Given an indexed collection and some terms, return ranked results."
  [idx & terms]
  (let [term-results (apply get-results-for-terms idx terms)
        ranked-results (rank-results term-results)]
    (sort-by second (fn [a b] (compare b a)) ranked-results)))

(defn search
  "Given a file name and some terms, return ranked results."
  [file-name & terms]
  (let [idx (read-index file-name)]
    (apply search-index idx terms)))
