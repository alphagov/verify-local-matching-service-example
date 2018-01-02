#!/bin/bash -eux

docker run --name postgres_for_matching_service -d -p 5432:5432 postgres

# wait for the database to come up
MAX_RETRIES=15
set +e
for i in $(seq 1 $MAX_RETRIES); do
    docker exec postgres_for_matching_service pg_isready -q -h localhost -U postgres
    s=$?
    test "$s" -eq 0 && break || echo "Waiting for postgres to start"; sleep 1
done
set -e

# Start example local matching service with database migration
./gradlew clean installDist
DB_URI="jdbc:postgresql://localhost:5432/postgres?user=postgres" ./build/install/verify-local-matching-service-example/bin/verify-local-matching-service-example &
