#!/bin/bash -eux

./startup.sh -x clean

./download_test_tool_from_artifactory.sh

unzip matching-service-test-tool-*.zip
cd verify-matching-service-test-tool-*

echo "Running test tools scenarios...."
./bin/verify-matching-service-test-tool -e ../examples -c ../verify-matching-service-test-tool.yml
