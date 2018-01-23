#!/bin/bash -eux

pwd=$PWD
function cleanup {
  docker-compose down -v
  rm -r verify-matching-service-test-tool-* matching-service-test-tool-*.zip
}
trap cleanup EXIT

./download_test_tool_from_artifactory.sh

docker-compose run app ./run_test_tool_scenarios.sh
