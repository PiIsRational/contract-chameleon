#!/bin/bash

echo "################################################################################"
echo "## Ensuring that KeY (standard Dynamic Frames variant) is executable"
echo "##"
echo "## This should print the KeY version number 2.12.4-dev (internal: 233aa...)"
echo "## and some status messages"
echo "## The full output is stored in key.out"
echo "################################################################################"
java  -jar tools/key-2.12.4-dev-exe.jar "--show-properties" | tee key.out | grep -v " - Property: "

echo
echo "################################################################################"
echo "## Ensuring that the Universe Encapsulation Type checker is executable"
echo "##"
echo "## This should print the underlying call to the compiler and the version of the"
echo "## Java compiler (21.0.10)."
echo "## The full output is stored in uet.out"
echo "################################################################################"
echo "Checking that the Universe Encapsulation Type checker is executable (should print the help message of the Java compiler) ..."
tools/uet-checker/checkEnc.sh --version 2>&1 | tee uet.out

echo
echo "################################################################################"
echo "## Ensuring that KeY (standard Dynamic Frames variant) is executable"
echo "##"
echo "## This should print the KeY version number 2.13.0-dev (internal: 86dbdf...)"
echo "## and some status messages"
echo "## The full output is stored in key-uet.out"
echo "################################################################################"
java -jar tools/key-2.12.4-UT-dev-exe.jar "--show-properties" | tee key-uet.out | grep -v " - Property: "

echo
echo "############################################################################"
echo "## Ensuring that contnuous integration tool for KeY is executable"
echo "##"
echo "## This should print the help screen for this tool."
echo "############################################################################"
java -cp tools/key-2.12.4-dev-exe.jar:tools/citool-1.7.0-SNAPSHOT-mini.jar io.github.wadoon.keycitool.CheckerKt --help
