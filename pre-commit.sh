#!/usr/bin/env bash
set -e

pwd=$PWD

function cleanup {
  cd $pwd
  ./shutdown.sh
  rm -rf verify-matching-service-test-tool-local/
}
trap cleanup EXIT

cd "$(dirname $0)"
./gradlew clean test intTest 

pushd ../verify-matching-service-adapter/verify-matching-service-test-tool >/dev/null
  ../gradlew clean installDist
  cp -R build/install/verify-matching-service-test-tool/ $pwd/verify-matching-service-test-tool-local/
popd >/dev/null

docker-compose run app ./run-test-tool-scenarios.sh
