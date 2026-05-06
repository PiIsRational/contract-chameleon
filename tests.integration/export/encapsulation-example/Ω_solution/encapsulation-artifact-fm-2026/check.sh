#!/bin/bash

# check everything except for VeriFast
./checkNoVeriFast.sh

echo
echo "################################################################################"
echo "## Checking the VeriFast part ... "
echo "################################################################################"
arch=$(uname -i)

if [ "$arch" == 'arm*' ]; then
echo "ARM architecture is not supported (no VeriFast binaries available). If you have a Mac ARM, you can try to run the binary directly. Try 'tools/vf/verifast-26.01-macos-aarch/bin/verifast -c -allow_dead_code -shared verifast/sources.jarsrc'"
exit -1
fi

tools/vf/verifast-26.01-linux-amd64/bin/verifast -c -allow_dead_code -shared verifast/sources.jarsrc
