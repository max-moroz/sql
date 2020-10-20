#!/bin/bash

#==========================================================
# Task 7 - SQL
# This should be at the same folder as University.jar
# This is for executing University application
#==========================================================


# Check for University.jar file
if [ ! -f $(dirname "$0")/University.jar ]; then
echo "Error: University.jar file is not found"
exit 1
fi

# Iterate through the list of given command line arguments
java -jar $(dirname "$0")/University.jar $@
exit 0

