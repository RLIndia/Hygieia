#!/bin/bash
mvn clean install -DskipTests -Dpmd.skip
cp catalyst.template target/application.properties
echo "dbhost=localhost" >> target/application.properties
cd target
java -jar catalyst-deployment-collector-2.0.2-SNAPSHOT.jar