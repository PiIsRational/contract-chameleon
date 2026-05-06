#!/bin/bash

# check that VeriFast is executable
echo "################################################################################"
echo "## Ensuring that VeriFast is executable"
echo "##"
echo "## This should print the version 26.01 of verifast and the beginning of the help message"
echo "## The full output is stored in verifast.out"
echo "################################################################################"
# should print a list of the available flags and parameters of VeriFast
./tools/vf/verifast-26.01-linux-amd64/bin/verifast --help 2>&1 | tee verifast.out | grep -v "^  -"

echo
# check that other tools are executable
./smokeTestNoVeriFast.sh
