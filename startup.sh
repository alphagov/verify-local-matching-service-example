#!/bin/bash -eux

# wait for the database to come up
sleep 10

# Start example local matching service with database migration
./gradlew clean installDist $@
DB_URI="jdbc:postgresql://db:5432/postgres?user=postgres" ./build/install/matchingservice/bin/matchingservice server verify-local-matching-service-example.yml &

sleep 10 # to give the above application time to start