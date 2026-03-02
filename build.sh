#!/bin/sh

#
# Build the project.
#
# It initialises all submodules if not yet done.
# It pulls from all submodules
# It builds the jml parser submodule
# It cleans and builds the main modules
#
# See README.md for details

# fetch and initialize the submodules the first time
git submodule update --init --recursive

# pull changes from submodules
git pull --rebase --recurse-submodules 

# publish custom jml parser
cd libs/jmlparser
./mvnw clean install -DskipTests
#check that snapshot exists
ls ~/.m2/repository/io/github/jmltoolkit/jmlparser-core/ 
cd ../..

# build with gradle
./gradlew clean build
