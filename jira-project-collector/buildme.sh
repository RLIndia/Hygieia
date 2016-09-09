#!/bin/bash
mvn clean install -DskipTests  -Dpmd.skip=true
cp application.properties target/.
cd target
java -jar jira-project-collector-2.0.2-SNAPSHOT.jar
cd ..