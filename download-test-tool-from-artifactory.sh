#!/bin/bash -eux

artifactory_name=matching-service-test-tool
server_location=https://artifactory.ida.digital.cabinet-office.gov.uk/artifactory/remote-repos/uk/gov/verify
version=`curl -s $server_location/$artifactory_name/maven-metadata.xml | grep release | sed "s/.*<release>\([^<]*\)<\/release>.*/\1/"`
zip_name=$artifactory_name-$version.zip

url="$server_location/$artifactory_name/$version/$zip_name"

echo "Downloading the test tool zip file..."
curl -O $url

unzip $zip_name