#!/bin/bash
echo "Stopping all running processes"
pkill java
pkill gulp
killall java
sleep 10


echo"Currently running java processes before starting Hygieia"
ps aux | grep java

sleep 5
 
cd api/target
echo "Starting API service"
java -jar api.jar --spring.config.location=./dashboard.properties &


echo "Starting Bitbucket  collector"
cd ../../bitbucket-scm-collector/target
java -jar bitbucket-scm-collector-2.0.2-SNAPSHOT.jar &

echo "Starting Jenkins collector"
cd ../../jenkins-build-collector/target
 java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar &

echo "Starting Chef collector"
cd ../../chef-collector/target
java -jar chef-collector-2.0.2-SNAPSHOT.jar & 


echo "Starting Jira collector"
cd ../../jira-project-collector/target
java -jar jira-project-collector-2.0.2-SNAPSHOT.jar &

echo "Starting Sonar collector"
cd ../../sonar-codequality-collector/target
java -jar sonar-codequality-collector-2.0.2-SNAPSHOT.jar &


echo "Starting Testrail collector"
cd ../../testrail-results-collector/target
java -jar testrail-results-collector.jar &

cd ../../UI
 sudo node_modules/gulp/bin/gulp.js serve &

echo "Done."
