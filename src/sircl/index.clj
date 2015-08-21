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

(defn vocabulary-for-document
  "Given a document vector, return its vocabulary.
See `document-vector' for an explanation what a document vector is.
The vocabulary is a set of words.
So `vocabulary-for-document' returns the set of words in the
document vector."
  [docvec]
  (set (keys docvec)))

(defn vocabulary
  "Maps terms to their document frequencies.
We will use this as a basis to get idf (inverse document frequency).
Expects a list of vocabularies (see `vocabulary-for-document')."
  [vocabularies]
  (frequencies (mapcat list* vocabularies)))
