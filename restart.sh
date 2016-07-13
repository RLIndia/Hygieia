#!/bin/bash
echo "Stopping all running processes"
killall java
sleep 5 
cd api/target
echo "Starting API service"
java -jar api.jar --spring.config.location=./dashboard.properties > /dev/null 2>&1 &

echo "Starting Jira collector"
cd ../../jira-feature-collector/target
java -jar jira-feature-collector.jar  > /dev/null 2>&1 &

echo "Starting Octopus collector"
cd ../../octopus-deployment-collector/target
java -jar octopus-deployment-collector-2.0.2-SNAPSHOT.jar > /dev/null 2>&1 &

echo "Starting Jenkins collector"
cd ../../jenkins-build-collector/target
java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar > /dev/null 2>&1 &

