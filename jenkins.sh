#!/bin/bash -eux

# Start example local matching service with database migration
./startup.sh

sleep 10 # to give the above application time to start

# Unpack, configure and start test tool
pushd ../verify-matching-service-test-tool
    unzip verify-matching-service-test-tool-*.zip
    cd verify-matching-service-test-tool-*

    rm -rf examples
    cp -r ../../verify-local-matching-service-example/examples .

cat <<EOF > verify-matching-service-test-tool.yml
localMatchingService:
  matchUrl: http://localhost:50500/match-user
EOF
    ./bin/verify-matching-service-test-tool

popd
./shutdown.sh