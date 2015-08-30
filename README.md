# sircl

Simple Information Retrieval using Clojure Language

This is just a toy project to learn Clojure.  It's my first Clojure
program.  I spent between 10 and 20 hours learning Clojure, and this
is the result.  I have background in other Lisps.

The idea is to take a directory full of text files, then run the
indexer which produces an index of the words in the files.  Given such
an index on disk, a search engine will take a sequence of words and
perform the search and return a ranked list of documents.

This was inspired by `perlindex`, but of course this is just a toy
whereas `perlindex` is a real program.

## Installation

Download from https://github.com/kgrossjo/sircl
Create an uberjar with `lein uberjar`.

## Usage for indexing

Creating an index works like this:

    $ java -jar sircl-0.1.0-standalone.jar index FILE ROOT

## Usage for searching

Searching a given index works like this:

    $ java -jar sircl-0.1.0-standalone.jar search FILE TERM...

## Options

* `index` or `search` are the command to execute.
* `FILE` is the name of the file housing the index.
  It is either written to (command `index`) or read
  from (command `search`).
* `ROOT` is the root directory of all documents.
  A document is a text file.
* `TERM` can be specified multiple times and gives a
  search term.

## Examples

You could download the CACM collection from
http://www.search-engines-book.com/collections/ and unpack it in some
local directory (say `/foo/CACM`).  You could then index this
collection, writing to the `cacm.idx` file, by running the following:

    $ java -jar sircl-0.1.0-standalone.jar index cacm.idx /foo/CACM

You could then search this index for the words "boolean" and "knuth"
like this:

    $ java -jar sircl-0.1.0-standalone.jar search cacm.idx boolean knuth

### Bugs

While the words in the document are normalized (lowercased, mostly),
this is not applied to the query.

There is no stopword elimination.

There is no stemming.

The formula used for ranking is pretty braindead.

There is no UI to speak of.

There is no real persistence layer, it's just cheating.  At least we
should be able to avoid reading the whole index into main memory every
time.  I think there is no point in using something sophisticated, but
maybe key/value store for the inverted index would be nice, or perhaps
also for the document vectors.

It would be nice to have incremental indexing, i.e. to make it
possible to add a document to the collection without redoing all the
work.  Hm.  I think we need to compute the document vector for the
added document and then we need to go through it and load each
inverted list and add the entry to it and then save the inverted list
again.  It would be even better to find some on-disk format that
allows us to augment the inverted list without having to read it into
main memory, but touching just the changed inverted lists is a step
forward.


## License

Copyright © 2015 Kai Grossjohann

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
