#!/bin/bash

# Compiles and runs all of the sample programs
# Displays the commands being run

mkdir -p target/
FILES=`find . -name *.java`
for f in $FILES
do
    fname=$(basename "$f")
    class=$(basename "$f" .java)
    cmd1="java -jar ../target/mini-javac.jar -d target $f"
    cmd2="java -cp target $class"
    cmdstr="${cmd1} &&\n${cmd2}"
    cmd="${cmd1} && ${cmd2}"
    echo -e $cmdstr
    eval $cmd
done
