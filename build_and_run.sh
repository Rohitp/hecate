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


echo "Removing old binary files in ${OUT}/*"
rm -rf "${OUT}"/*




echo "Compiling"

javac -d "${OUT}" $(find . -name "*.java")

if [[ $? -ne 0 ]]; then
    echo "Compilation failed!"
    exit 1
fi

jar cfm "${OUT}/${JAR_NAME}" MANIFEST.MF -C bin .



if [[ $? -ne 0 ]]; then
    echo "Failed to create jar file!"
    exit 1
fi


echo "Generated artifact ${JAR_NAME} in ${OUT}"

echo "Running artifact"

java -jar "${OUT}/${JAR_NAME}" "$1"

if [[ $? -ne 0 ]]; then
    echo "Failed to run the jar file!"
    exit 1
fi