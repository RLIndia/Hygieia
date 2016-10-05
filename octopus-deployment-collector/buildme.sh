#!/bin/bash
mvn clean install -DskipTests -Dpmd.skip
cp octopus.template target/application.properties
echo "dbhost=localhost" >> target/application.properties
cd target
java -jar octopus-deployment-collector-2.0.2-SNAPSHOT.jar