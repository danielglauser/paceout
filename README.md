# paceout

An account number parser.

## Details

### Phase 1

The paceout account number parser expects the account numbers to be in a file, one per line comprised of ASCII pipes, underscores, and spaces. For example:

```
 _  _  _  _  _  _  _  _  _ 
| || || || || || || || || |
|_||_||_||_||_||_||_||_||_|
```                         

Each digit is made of nine characters, three per line. Trailing spaces are not trimmed. For example, the first line of a zero is " _ ", not " _". The parser is somewhat resiliant, if it doesn't recognize a digit it should replace it with a question mark and continue parsing both the number and the remainder of the file. Files are expected to contail about 500 account numbers.

### Phase 2

Account numbers are expected to be of a certain format, namely they are supposed to pass the following checksum:

account number:  3  4  5  8  8  2  8  6  5
position names:  d9 d8 d7 d6 d5 d4 d3 d2 d1

checksum calculation:

((1*d1) + (2*d2) + (3*d3) + ... + (9*d9)) mod 11 == 0

### Phase 3

When passed a certain flag the parser should emmit an output file listing what it understands. The output file has one account number per row. If some characters are illegible, they are replaced by a ?. In the case of a wrong checksum, or illegible number, this is noted in a second column indicating status.

457508000
664371495 ERR
86110??36 ILL

## Usage

### Building

Before execution you must first build the code. The code is built with [Leiningen](http://http://leiningen.org/) using the Java Virtual Machine. If you have Leiningen and Java installed all you have to do is type:

```
lein uberjar
```

### Execution

./bin/parse file_containing_a_bunch_of_account_numbers.txt

Will write a bunch of account numbers to standard out. Optionally:

./bin/parse -o output_file.parsed file_containing_a_bunch_of_account_numbers.txt

Will write the output to the named output file, including the above status in the case of an error. The output file will be written to the path specified. Relative paths start at the current working directory.

### Common Errors

`Error: Could not find or load main class paceout.main` means that you forgot to build the code with `lein uberjar`.

## Development

### Source

This code isis written in Clojure and managed with [Leiningen](http://http://leiningen.org/). It is kicked off from a bash shell script in `bin`. This code has been tested on Linux but should work on any system where bash, Java, and Leiningen work. To build from source:

```
cd paceout
lein unberjar
```

This packages the Clojure source into a self contained executable jar that can be run from the `bin/parse` shell script.

### Testing

Like any Leiningen project the tests can be run with `lein test`.

## License

Copyright © 2015 Daniel Glauser

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
