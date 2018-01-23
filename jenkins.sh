#!/bin/bash -eux

pwd=$PWD
function cleanup {
  docker-compose down -v
  rm -r verify-matching-service-test-tool-* matching-service-test-tool-*.zip
}
trap cleanup EXIT

./download-test-tool-from-artifactory.sh

docker-compose run app ./run-test-tool-scenarios.sh
