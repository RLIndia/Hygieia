#!/bin/bash
echo "Terminating current API"
kill -9 $(ps -aux | grep java | grep api.jar | grep spring | awk '{print $2}')
mvn clean install -DskipTests -Dpmd.skip
echo "Configuring API"
cp -f dashboard.template target/dashboard.properties
echo "dbhost="$1 >> target/dashboard.properties
cd target
java -jar api.jar --spring.config.location=./dashboard.properties
