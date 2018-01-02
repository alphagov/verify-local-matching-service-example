#!/bin/bash -eux

shutdown="$(realpath shutdown.sh)"
trap "sh $shutdown" EXIT

# Start example local matching service with database migration
./startup.sh

sleep 10 # to give the above application time to start

# Unpack, configure and start test tool
pushd ../verify-matching-service-test-tool
    unzip verify-matching-service-test-tool-*.zip
    cd verify-matching-service-test-tool-*

    ./bin/verify-matching-service-test-tool -e ../../verify-local-matching-service-example/examples -c ../../verify-local-matching-service-example/verify-matching-service-test-tool.yml
popd
