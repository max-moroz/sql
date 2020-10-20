#!/bin/bash

#==============================================================
# Task 7 - SQL
# Execute from project's root folder
# This is for building project and placing it into newly created directory /dist
# together with all source files and uber-jar
# Java archive is not created if any test failed or code coverage not been met.
#==============================================================

./mvnw clean package

mkdir dist
cp University.cmd University.sh _README.md ./dist
cd target
cp University.jar ../dist
exit 0

