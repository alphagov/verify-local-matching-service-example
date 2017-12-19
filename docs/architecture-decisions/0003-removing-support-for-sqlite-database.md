# 3. Removing support for sqlite database

Date: 2017-12-18

## Status

Accepted

## Context
Initially, we decided to use SQlite since it uses local storage as database for applications. So people who want to use 
this application does not have to depend on a database running on their system. We also supported Postgres to make the application
run in PAAS(cloud) environment. The assumption behind it was JDBI can handle access to different databases but there were cases
where we to had handle sql differently to make it work with both databases. 

## Decision

We decided to remove the support of SQlite since it was very difficult to write queries that work with both the databases and
also since stub relaying party has to access the database it would be appropriate to use separate database instead of 
local storage as database.


## Consequences

People who want to start this application should have Postgresql database running on their system.
