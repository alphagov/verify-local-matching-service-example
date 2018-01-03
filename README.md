# Example local matching service for GOV.UK Verify

This is an example local matching service built by the GOV.UK Verify team to demonstrate how a local matching service works and help services building their own. Every government service connecting to Verify must build their own local matching service.

:warning: _This is an example implementation and is not production-ready._ :warning:

For more guidance on the role of a local matching service and how to build one, refer to the:
* [Verify technical guide](http://alphagov.github.io/rp-onboarding-tech-docs/pages/ms/ms.html)
* [development stories for this example](https://github.com/alphagov/verify-local-matching-service-example/projects/1)

## Background

This example uses a simple database and matching strategy. It has not been designed to be used in front of a real database.

### Database

This example local matching service uses a SQL database. Refer to the [database schema](/docs/schema.png) for details.

All SQL has been written for Postgres and has not been tested with any engine.

### Matching strategy

This example implementation follows a simplified matching strategy from DWP.

The example uses the following attributes:

* firstname
* surname
* date of birth
* postcode
* National Insurance number

**Matching cycles**

This example follows [Cycle 0, Cycle 1 and Cycle 3 match cycles](http://alphagov.github.io/rp-onboarding-tech-docs/pages/ms/msWorks.html).

It will first run Cycle 0. The Matching Service Adapter will provide a hashed persistent identifier (PID). The cycle will then check the PID can be matched to an existing PID in the database. This cycle only works if the user has previously verified their identity with the same identity provider.

If Cycle 0 fails, it will run Cycle 1 using surname and date of birth. The example will disambiguate similar matches with postcode and then firstname. All four attributes need to match in order to return `match`.

If the request contains a National Insurance number, the example will run Cycle 3. Using National Insurance number as a Cycle 3 attribute was an arbitrary decision for the purposes of this example. When building your own local matching service, you should use other attributes.

If the example finds a match in Cycles 1 or 3 it will store the PID in the database.

**Caveats**

This example local matching service will only return `match` or `no match`. It does not provide any fuzzy matching, for example matching `ONeill` to `O'Neill` or `Will` to `William`. The example does not provide any means of logging or investigating results.

The example ignores any attributes where `verified` is set to `false`. This would be a suitable starting point for any service with an existing dataset expected to include most new users.

The example is case insensitive and will remove any spaces in requests containing postcodes.

## Run

:warning: _This is an example implementation and is not production-ready. Contact the Verify team if you'd like any support with your matching strategy or local matching service._ :warning:

**Prerequisites**

To run the example local matching service locally, you'll need:
* Java 8
* Docker
* working version of [Verify Matching Service Test Tool](https://github.com/alphagov/verify-matching-service-adapter/tree/master/verify-matching-service-test-tool)

### Build

To run the matching service without a clean database, run the following then execute the script in install/bin [need andy]

```
./gradlew installDist
```

To run the matching service with a clean data, run

```
./startup.sh
```

### Configuration

You can configure the local matching service using environment variables or command line arguments.

**Environment variables**

```
PORT - port to start matching service on (default is 50500)
LOG_LEVEL - logging level for matching service (default is INFO) - can be (DEBUG, INFO, WARNING, ERROR) going down in verbosity from left -> right
DB_URI - JDBC connection URI to database to run migrations and connect to. Format is “jdbc:postgresql://host-name:port/database-name?user=user-name&password=password”
```

**Command line arguments**
If you don't want to use the default config file or environment variables, you can specify the location of the config file using `./bin/verify-local-matching-service-example server /path/to/config.yml`

## Testing

You can run:
* unit and integration tests with gradle
* specific local matching service test tool tests

To run all tests:

```
pre-commit.sh
```

**For unit and and integration tests only**

```
./gradlew test intTest
```

**For Matching service tests only**

The [Verify Matching Service Test Tool](https://github.com/alphagov/verify-matching-service-adapter/tree/master/verify-matching-service-test-tool) helps you check your local matching service can:

* handle matching datasets
* find and match records correctly
* handle matching failures

You can use the test tool while building your local matching service and include it any automated testing.

This example uses the test tool. See the [examples folder](/examples/) for example tests.  

## Support and raising issues

Contact the Verify team if you'd like any support with your matching strategy or local matching service via idasupport+onboarding@digital.cabinet-office.gov.uk.

**Security issues**

If you think you have discovered a security issue in this code please email disclosure@digital.cabinet-office.gov.uk with details.

For non-security related bugs and feature requests please raise an issue in the [GitHub issue tracker](https://github.com/alphagov/verify-local-matching-service-example/issues).

## Licensing

[MIT License](https://github.com/alphagov/verify-local-matching-service-example/blob/master/LICENSE)
