#!/bin/zsh

SRC="./src"
OUT="./bin"
JAR_NAME="hecate.jar"
MAIN_CLASS="com.babel.hecate.Hecate"
PARSE_FILE_NAME="./tests/parsertest-class.txt"

if [[ $# -ne 1 ]] ; then
    echo 'Pass a text file with the source code as an argument. Only one argument'
    exit 0
fi


java -jar "${OUT}/${JAR_NAME}" "$1"