#!/bin/bash -eux

pwd=$PWD
function cleanup {
  docker-compose down -v
}
trap cleanup EXIT

docker-compose run app ./run_test_tool_scenarios.sh
