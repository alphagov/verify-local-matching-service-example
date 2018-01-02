#!/usr/bin/env bash

./startup.sh
sleep 5

docker run -d --network=db_network --name schemacrawler -v $(pwd)/docs:/share --rm -t --entrypoint=/bin/bash sualeh/schemacrawler
# There is an issue with graphviz output in HTML mode that means this line must be removed
docker exec schemacrawler sed -i "s/schemacrawler.graph.graphviz_opts=-Gdpi=300//" config/schemacrawler.config.properties
docker exec schemacrawler ./schemacrawler.sh -server=postgresql -host=postgres_for_matching_service -user=postgres -database=postgres -infolevel=standard -command=schema -outputformat=htmlx -o /share/schema.html -grepcolumns=".*schema_version.*" -invert-match -title="Example Local Matching Service Database Schema"
docker exec schemacrawler ./schemacrawler.sh -server=postgresql -host=postgres_for_matching_service -user=postgres -database=postgres -infolevel=standard -command=schema -outputformat=png -o /share/schema.png -grepcolumns=".*schema_version.*" -invert-match -title="Example Local Matching Service Database Schema"

docker kill schemacrawler
./shutdown.sh

