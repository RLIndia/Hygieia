#!/bin/bash
cp application.properties target/.
mvn install -DskipTests -Dpmd.skip
cd target
java -jar catalyst-deployment-collector-2.0.2-SNAPSHOT.jar