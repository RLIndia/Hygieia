#!/usr/bin/env bash
mvn clean
mvn install -DskipTests -Dpmd.skip
cp testrail.template target/application.properties
echo "dbhost=localhost" >> target/application.properties
cd target
java -jar testrail-results-collector.jar