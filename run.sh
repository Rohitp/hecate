#!/bin/zsh

SRC="./src"
OUT="./bin"
JAR_NAME="hecate.jar"
MAIN_CLASS="com.babel.hecate.Hecate"
PARSE_FILE_NAME="./tests/parsertest-class.txt"


java -jar "${OUT}/${JAR_NAME}" "${PARSE_FILE_NAME}"