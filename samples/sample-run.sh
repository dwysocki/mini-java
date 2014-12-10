#!/bin/bash

__compilers__="mini-javac javac"
function compile {
    __cmd1=$1
    __cmd2=$2
    # display and evaluate compile/run command
    echo -e "${__cmd1} &&\n${__cmd2}"
    eval "${__cmd1} && ${__cmd2}"
}

function compile_switch {
    case "$1" in
        mini-javac)
            compile "java -jar ../target/mini-javac.jar -d target $2" \
                    "java -cp target $3"
            ;;
        javac)
            compile "javac -d target $2" \
                    "java -cp target $3"
            ;;
        all)
            # compile all
            compile_switch mini-javac $2 $3
            compile_switch      javac $2 $3
            ;;
        *)
            echo "Usage: $0 {mini-javac|javac|all}"
            exit 1
    esac
}


# Compiles and runs all of the sample programs
# Displays the commands being run

mkdir -p target/
FILES=`find . -name *.java`
for f in $FILES
do
    fname=$(basename "$f")
    class=$(basename "$f" .java)
    compile_switch $1 $f $class
done
