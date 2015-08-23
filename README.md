# sircl

Simple Information Retrieval using Clojure Language

This is just a toy project to learn Clojure.  It's my first Clojure
program.  I spent about 20 hours learning Clojure, and this is the
result.  I have background in other Lisps.

The idea is to take a directory full of text files, then run the
indexer which produces an index of the words in the files.  Given such
an index on disk, a search engine will take a sequence of words and
perform the search and return a ranked list of documents.

Create an uberjar with `lein uberjar`.

## Installation

Download from https://github.com/kgrossjo/sircl

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

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
