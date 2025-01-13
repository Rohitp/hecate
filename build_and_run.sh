#!/bin/zsh

SRC="./src"
OUT="./bin"
JAR_NAME="hecate.jar"
MAIN_CLASS="com.babel.hecate.Hecate"
PARSE_FILE_NAME="./tests/parsertest.txt"

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

java -jar "${OUT}/${JAR_NAME}" "${PARSE_FILE_NAME}"

if [[ $? -ne 0 ]]; then
    echo "Failed to run the jar file!"
    exit 1
fi