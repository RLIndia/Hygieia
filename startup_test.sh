#!/bin/bash


echo "Fetching Token"
request="--post-data={\"username\":\"$3\",\"pass\":\"$4\",\"authType\":\"token\"} $1/auth/signin"
wget -S --header='Accept-Charset: UTF-8' --header='Content-Type: application/json' -O response.json $request
token=$(sed -e 's/^.*"token":"\([^"]*\)".*$/\1/' response.json)
echo $token
rm response.json

cd api/target

echo "Configuring Chef collector"
cd ../../chef-collector/
cp -f jenkins.template target/application.properties
wget --header="Accept-Charset: UTF-8"  --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/20 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
nohup java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar &