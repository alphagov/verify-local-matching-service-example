#!/bin/bash -eux

./startup.sh -x clean

cd verify-matching-service-test-tool-*

echo "Running test tools scenarios...."
./bin/verify-matching-service-test-tool -e ../examples -c ../verify-matching-service-test-tool.yml
