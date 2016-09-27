#!/bin/bash
echo "Stopping all running processes"
pkill java
pkill gulp
killall java
sleep 5 
cd api/target
echo "Starting API service"
nohup java -jar api.jar --spring.config.location=./dashboard.properties &

echo "Starting Octopus collector"
cd ../../octopus-deployment-collector/target
nohup java -jar octopus-deployment-collector-2.0.2-SNAPSHOT.jar &

echo "Starting Bitbucket  collector"
cd ../../bitbucket-scm-collector/target
nohup java -jar bitbucket-scm-collector-2.0.2-SNAPSHOT.jar &

echo "Starting Jenkins collector"
cd ../../jenkins-build-collector/target
nohup java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar &

echo "Starting Functional collector"
cd ../../sbux-functional-test-collector/target
nohup java -jar sbux-functional-test-collector-2.0.2-SNAPSHOT.jar &


echo "Starting Jira collector"
cd ../../jira-feature-collector/target
java -jar jira-feature-collector.jar  > /dev/null 2>&1 &

<<'COMMENT'
echo "Starting Sonar collector"
cd ../../sonar-codequality-collector/target
java -jar sonar-codequality-collector-2.0.2-SNAPSHOT.jar > /dev/null 2>&1 &

COMMENT

echo "Done."
