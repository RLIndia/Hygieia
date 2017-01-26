#!/bin/bash
mvn clean install -DskipTests  -Dpmd.skip=true
cp jira.template target/application.properties
echo "Configuring Jira  collector"
wget $1/d4dMastersCICD/readmasterjsonnew/23 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
cat application.properties
java -jar jira-project-collector-2.0.2-SNAPSHOT.jar -Djavax.net.ssl.trustStore=trustStore
cd ..