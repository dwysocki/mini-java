# mini-java

A MiniJava compiler implemented in Clojure.


## Running

```bash
$ lein antlr4
$ lein uberjar
$ java -jar target/mini-javac.jar --help
Usage: mini-javac [options] action

Options:
      --syntax            Stop after syntax checking
      --static-semantics  Stop after static semantics checking
  -h, --help

```
