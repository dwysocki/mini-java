# mini-java

A MiniJava compiler implemented in Clojure. This was a semester-long project
for the Fall 2014 section of CSC444 at SUNY Oswego. Any MiniJava program
according to the
[grammar specification](
  http://www.cambridge.org/resources/052182060X/MCIIJ2e/grammar.htm)
given by Cambridge University can be compiled down to JVM bytecode. In addition
to the Cambridge grammar, my own personal addition to the language -- a recur
statement -- is correctly parsed and compiled.


## Running

To run from the JAR, simply download the JAR from the final release, and
run with `java -jar mini-javac.jar --help`.

To build from source, first ensure that Leiningen 2.0 is installed and on your
classpath, then run as follows:

```bash
$ lein antlr4
$ lein uberjar
$ java -jar target/mini-javac.jar --help
Usage: mini-javac [options] filename

Options:
  -d, --directory DIR     .  Destination directory for class files
      --syntax               Stop after syntax checking
      --static-semantics     Stop after static semantics checking
  -h, --help
```

Sample MiniJava programs are provided in the `samples/` directory.
Run the `sample-run.sh` bash script to compile and run all samples.


## Implementation

Source code parsing is accomplished using [ANTLR4](http://www.antlr.org/) to
create an [AST](https://en.wikipedia.org/wiki/Abstract_syntax_tree), according
to the grammar specified in `src/antlr/MiniJava.g4`. Useful messages are
output in the event of parse errors, indicating the location of the error and
providing some insight into the possible cause, much like `javac`.
A correctly parsed AST is then transformed into a more desirable form using the
functions in the `mini-java.ast` namespace.

Static semantics (type checking and name resolution) was performed on the
AST using functions I wrote in the `mini-java.static-semantics` namespace.
This ensures that no errors beyond mere syntax errors survived until
compilation time. 

After static semantics checking succeeds, the AST is transformed once again,
in the `mini-java.code-gen` namespace, this time into valid JVM bytecode.
This process was aided by the use of the robust [ASM 5](http://asm.ow2.org/)
library. Valid Java `.class` files are output at the end of this process,
which can be run using `java`. This has been tested on OpenJDK 1.7.0, but
in theory it should work on any standard Java implementation starting with
JDK 1.1.0.


## Language Feature Addition

As part of the project, we were required to choose one addition to make to the
language. I chose to make a `recur` statement for handling tail recursion. The
statement resembles the ternary operator from C-family languages, but uses
it in a very different way. An example usage of the statement is given in the
factorial program below:

```java
public int factorial(int n) {
    return this.factorial_iter(n, 1);
}

public int factorial_iter(int n, int result) {
    recur 0 < n ? (n-1, n*result) : result;
}
```

The statement is broken up into 3 parts. The first is the predicate, `0 < n`,
which determines which of the two following parts are to be evaluated.
If the predicate is true, the recursive case is evaluated, `(n-1, n*result)`,
and the method recurs with the given bindings. If the predicate is false, the
base case is evaluated, `result`, and returned. Since MiniJava only allows
return statements at the end of a method, this made it simple to implement
tail call optimization. As a result, any use of the `recur` statement is
automatically tail call optimized.


## Credit

Daniel Wysocki © 2014 - MIT License

Special thanks goes to Dr. Doug Lea, the instructor who helped make this
possible. I'd also like to thank Terence Parr for developing the ANTLR parsing
tool which I wouldn't have survived without, the folks from the ObjectWeb
consortium for developing ASM which made bytecode generation a breeze, and the
Clojure community for creating the language which made writing this compiler a
joy.
