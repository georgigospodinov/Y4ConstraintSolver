#!/usr/bin/env bash

# The Java source files
readonly SRC_FILES="src/*/*.java src/*/*/*.java"

# The directory where compiled class files are temporary stored.
readonly CLASSES_DIR="bin"

# THe library for parsing and evaluating mathematical expressions
readonly LIBS="libs/MathParser.org-mXparser-v.4.2.0-jdk.1.8.jar"

# The directory where the javadoc will appear
readonly JAVADOC_DIR="javadoc"

# Javadoc generation options
readonly JAVADOC_OPTIONS="-private -splitindex -use -author -version"

# The directory containing csp files
readonly PROBLEMS_DIR="problems/"

function create_javadoc {
    # Delete previous compilation
    rm -rf ${JAVADOC_DIR}

    # Create directories
    mkdir ${JAVADOC_DIR}

    # Generate JavaDoc
    javadoc ${JAVADOC_OPTIONS} -d ${JAVADOC_DIR} -sourcepath src -cp ${LIBS} -subpackages . >/dev/null 2>/dev/null
}

function compile {
    rm -rf ${CLASSES_DIR}
    mkdir ${CLASSES_DIR}
    javac ${SRC_FILES} -d ${CLASSES_DIR} -cp ${LIBS}
}

function clean {
    rm -rf ${CLASSES_DIR} ${JAVADOC_DIR}
}

function generateQueens {
    amount=$1
    java -cp ${CLASSES_DIR} generators.QueensGenerator ${amount} > ${PROBLEMS_DIR}${amount}Queens.csp
}

function generateLangfords {
    k=$1
    n=$2
    java -cp ${CLASSES_DIR} generators.LangfordsGenerator ${k} ${n} > ${PROBLEMS_DIR}L${k}_${n}.csp
}

function generateSudoku {
    n=$1
    java -cp ${CLASSES_DIR} generators.SudokuGenerator > ${PROBLEMS_DIR}Sudoku${n}.csp
}

if [[ $# -eq 0 ]]; then
    compile
elif [[ "$1" = "compile" ]]; then
    compile
elif [[ "$1" = "javadoc" ]]; then
    create_javadoc
elif [[ "$1" = "clean" ]]; then
    clean
elif [[ "$1" = "queens" ]]; then
    generateQueens $2
elif [[ "$1" = "langfords" ]]; then
    generateLangfords $2 $3
elif [[ "$1" = "sudoku" ]]; then
    generateSudoku $2
else
    # The run command
    java -cp ${CLASSES_DIR}:${LIBS} $@
fi
