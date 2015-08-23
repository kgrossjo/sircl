(ns sircl.index
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]))

(defn normalize-word
  "Given a sequence of word characters, normalize this as a word.
Remove apostrophes from beginning and end.  (This is needed to handle
  words such as don't which contain apostrophes.)"
  [word]
  (-> word
      (str/replace-first #"^'" "")
      (str/replace-first #"'$" "")
      (str/lower-case)))

(defn string-words
  "Given a string, return a list of words in that string."
  [content]
  (filter (fn [x] (not (str/blank? x)))
          (map normalize-word
               (str/split content (re-pattern "([^a-zA-Z0-9'])+")))))

(defn file-words
  "Read the given file and return a list of words in that file."
  [file-name]
  (string-words (slurp file-name)))

(defn document-vector
  "Maps terms to their frequencies.
Yes, it's not really a document vector.
Input is a list of words, output is a map from words to frequencies."
  [words]
  (frequencies words))

(defn document-vector-from-file
  "Returns a document vector for a file.
See `document-vector'."
  [file-name]
  (document-vector (file-words file-name)))

(defn make-document-from-file
  "Returns a Document record from a file."
  [file-name]
  (print "Making document from file ")
  (println (.getPath file-name))
  (let [content (slurp file-name)
        vector (document-vector (string-words content))]
    {:type :document,
     :filename (.getPath file-name),
     :content content,
     :vector vector}))

(defn lookup-term-in-document
  "Look up the given term in the given document, return term frequency.
  Return nil if term is not in document."
  [term document]
  (get (:vector document) term))

(defn vocabulary-for-document
  "Given a document vector, return its vocabulary.
  See `document-vector' for an explanation what a document vector is.
  The vocabulary is a set of words.
  So `vocabulary-for-document' returns the set of words in the
  document vector."
  [document]
  (let [docvec (:vector document)]
    (set (keys docvec))))

(defn vocabulary
  "Maps terms to their document frequencies.
We will use this as a basis to get idf (inverse document frequency).
Expects a list of Documents."
  [documents]
  (println "Computing vocabulary")
  (frequencies (mapcat list* (map vocabulary-for-document documents))))

(defrecord DocumentCollection [directory documents vocabulary])

(defn make-collection
  [directory & {:keys [nfiles] }]
  (let [tree (file-seq (io/file directory))
        files (filter (fn [x] (.isFile x)) tree)
        maybe-shortened (if nfiles (take nfiles files) files)
        documents (map make-document-from-file files)]
    {:type :document-collection,
     :directory directory,
     :documents documents,
     :vocabulary (vocabulary documents)}))

(defn get-inverted-list-entry
  [term document]
  (let [x (lookup-term-in-document term document)]
    (if x
      {(:filename document) x}
      nil)))

(defn term-index
  "Returns an inverted list for `term'.
  Second arg `documents' is a collection of `Document' records."
  [term documents]
  (apply merge (map (fn [d] (get-inverted-list-entry term d))
                    documents)))

(defn inverted-index
  "Maps terms to the documents they appear in, with term frequency.
Use file names to identify documents."
  [collection]
  (apply hash-map (mapcat (fn [t] [t (term-index t (:documents collection))]) (keys (:vocabulary collection)))))

(defn make-indexed-collection
  [document-collection]
  {:type :indexed-collection,
   :collection document-collection,
   :index (inverted-index document-collection)})

(defn write-index-collection-to-file
  [indexed-collection file-name]
  (println "Writing index to disk")
  (spit file-name indexed-collection))

(defn read-index-collection-from-file
  [file-name]
  (slurp file-name))

(defn index-directory [directory-name file-name]
  (let [coll (make-collection directory-name)
        idx (make-indexed-collection coll)]
    (write-index-collection-to-file idx file-name)))
