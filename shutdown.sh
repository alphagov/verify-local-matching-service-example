#!/bin/bash -eux

lsof -ti:50500 | xargs kill
docker rm $(docker stop $(docker ps -a -q --filter="name=postgres_for_matching_service"))
docker network rm db_network
