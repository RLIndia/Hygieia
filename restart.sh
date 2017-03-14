#!/bin/bash
echo "---------------------------"
echo 'RESTARTING HYGIEIA SERVICES'
echo "---------------------------"

echo "Stopping all running processes"
pkill java
#pkill gulp
killall java
sleep 5 
cd api/target

echo "Starting API service"
nohup java -jar api.jar --spring.config.location=../dashboard.properties &

echo "Starting Jira collector"
cd ../../jira-project-collector/target
nohup java -jar jira-project-collector-2.0.2-SNAPSHOT.jar --spring.config.location=../application.properties &

echo "Starting Bitbucket collector"
cd ../../bitbucket-scm-collector/target
nohup java -jar bitbucket-scm-collector-2.0.2-SNAPSHOT.jar --spring.config.location=../application.properties &

echo "Starting Jenkins collector"
cd ../../jenkins-build-collector/target
nohup java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar --spring.config.location=../application.properties &

echo "Starting Chef collector"
cd ../../chef-collector/target
nohup java -jar chef-collector-2.0.2-SNAPSHOT.jar  --spring.config.location=../application.properties &

echo "Starting Sonar collector"
cd ../../sonar-codequality-collector/target
nohup java -jar sonar-codequality-collector-2.0.2-SNAPSHOT.jar  --spring.config.location=../application.properties &

echo >> 'Currently Running Java Process' 
ps aux | grep java
sleep 10
echo "Done."
