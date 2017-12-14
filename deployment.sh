#!/usr/bin/env bash
set -e

ROOT_DIR="$(dirname "$0")"
cd "$ROOT_DIR"

function cleanup {
  rm -rf "$ROOT_DIR/work"
}
trap cleanup EXIT


cfLogin() {
  if [ -z "${CF_USER:-}" ]; then
    echo "Using cached credentials in ${CF_HOME:-home directory}" >&2
  else
    CF_API="${CF_API:?CF_USER is set - CF_API environment variable also needs to be set}"
    CF_ORG="${CF_ORG:?CF_USER is set - CF_ORG environment variable also needs to be set}"
    CF_SPACE="${CF_SPACE:?CF_USER is set - CF_SPACE environment variable also needs to be set}"
    CF_PASS="${CF_PASS:?CF_USER is set - CF_PASS environment variable also needs to be set}"

    # CloudFoundry will cache credentials in ~/.cf/config.json by default.
    # Create a dedicated work area to avoid contaminating the user's credential cache
    export CF_HOME="$ROOT_DIR/work"
    rm -rf "$CF_HOME"
    mkdir -p "$CF_HOME"
    echo "Authenticating to CloudFoundry at '$CF_API' ($CF_ORG/$CF_SPACE) as '$CF_USER'" >&2
    cf api "$CF_API"
    # Like 'cf login' but for noninteractive use
    cf auth "$CF_USER" "$CF_PASS"
    cf target -o "$CF_ORG" -s "$CF_SPACE"
  fi
}

cfSetDatabaseUri() {
   cf unset-env verify-local-matching-service-example DB_URI
   LOCAL_DB_URI="$(cf env verify-local-matching-service-example | grep -o '"jdbc:postgresql://[^"]*' | tr -d '"' |sed 's/\\u0026/\&/g')"
   cf set-env verify-local-matching-service-example DB_URI "$LOCAL_DB_URI"
}

cfBindWithDatabase() {
    cf bind-service verify-local-matching-service-example verify-local-matching-service-example-db
}

./gradlew clean test intTest distZip

cfLogin
cfBindWithDatabase
cfSetDatabaseUri

cf push -f manifest.yml -p build/distributions/verify-local-matching-service-example.zip

