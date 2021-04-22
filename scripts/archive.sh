#!/bin/bash -e
# This is implied by exec:java goal, but let's do it first, to avoid mixing output from different goals.
mvn compile
ARCHIVE=target/archive
mkdir -p $ARCHIVE
# Test that the CLI works at all.
mvn exec:java -Dexec.args="version"
# Check fast output first.
mvn exec:java -q -Dexec.args="" >$ARCHIVE/help.txt
mvn exec:java -q -Dexec.args="version" >$ARCHIVE/version.txt
# This will produce progress messages from the CLI.
mvn exec:java -q
# Run again to get clean benchmark output.
mvn exec:java -q >$ARCHIVE/benchmark.txt

