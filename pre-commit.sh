#!/usr/bin/env bash
set -e

shutdown="$(realpath shutdown.sh)"
trap "sh $shutdown" EXIT

cd "$(dirname $0)"
./gradlew clean test intTest 

./startup.sh

pushd ../verify-matching-service-adapter/verify-matching-service-test-tool >/dev/null
  ../gradlew clean installDist
  ./build/install/verify-matching-service-test-tool/bin/verify-matching-service-test-tool -e ../../verify-local-matching-service-example/examples -c ../../verify-local-matching-service-example/verify-matching-service-test-tool.yml
popd >/dev/null
