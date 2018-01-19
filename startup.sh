#!/bin/bash -eux

# wait for the database to come up
MAX_RETRIES=15
set +e
for i in $(seq 1 $MAX_RETRIES); do
    psql -h localhost -U "postgres" -c '\q';
    s=$?
    test "$s" -eq 0 && break || echo "Waiting for postgres to start"; sleep 5
done
echo "Postgres is up"
set -e

# Start example local matching service with database migration
./gradlew clean installDist $@
DB_URI="jdbc:postgresql://db:5432/postgres?user=postgres" ./build/install/myapp/bin/myapp server verify-local-matching-service-example.yml &

sleep 10 # to give the above application time to start