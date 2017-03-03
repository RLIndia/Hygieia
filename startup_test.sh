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
cp -f chef.template target/application.properties
wget --header="Accept-Charset: UTF-8"  --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/10 -O target/temp.properties
chefId=$(sed -n 's/.*chef.id *= *\([^ ]*.*\)/\1/p' < target/temp.properties)
echo $chefId
wget --header="Accept-Charset: UTF-8"  --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/chef/pemFile/$chefId -O target/chef.pem

cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
echo "chef.pemFilePath="$(pwd)/target/chef.pem >> target/application.properties
cd target
nohup java -jar chef-collector-2.0.2-SNAPSHOT.jar &