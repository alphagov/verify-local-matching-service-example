#!/bin/bash -eux

pwd=$PWD
function cleanup {
  docker-compose down -v
  rm -r verify-matching-service-test-tool-* matching-service-test-tool-*.zip
}
trap cleanup EXIT

docker-compose build app
docker-compose run app ./run-test-tool-scenarios.sh
