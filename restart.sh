#!/bin/bash

#!/bin/bash

echo "------------------------------"
echo 'RE-STARTING HYGIEIA SERVICES'
echo "------------------------------"

echo "Stopping all running processes"
pkill java
#pkill gulp
killall java

sleep 10 
echo 'Currently Running Java Processes'
ps aux | grep java

cd api/target

echo "Starting API service"
nohup java -jar api.jar --spring.config.location=../dashboard.properties &


echo "Starting Bitbucket  collector"
cd ../../bitbucket-scm-collector/target
nohup java -jar bitbucket-scm-collector-2.0.2-SNAPSHOT.jar --spring.config.location=../application.properties &

echo "Starting Jenkins build collector"
cd ../../jenkins-build-collector/target
nohup java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar --spring.config.location=../application.properties &

echo "Starting Jenkins cucumber collector"
cd ../../jenkins-cucumber-test-collector/target
nohup java -jar jenkins-cucumber-test-collector-2.0.2-SNAPSHOT.jar --spring.config.location=../application.properties &


echo "Starting Chef collector"
cd ../../chef-collector/target
nohup java -jar chef-collector-2.0.2-SNAPSHOT.jar  --spring.config.location=../application.properties &


echo "Starting Jira collector"
cd ../../jira-project-collector/target
nohup java -jar jira-project-collector-2.0.2-SNAPSHOT.jar  --spring.config.location=../application.properties &

echo "Starting Sonar collector"
cd ../../sonar-codequality-collector/target
nohup java -jar sonar-codequality-collector-2.0.2-SNAPSHOT.jar  --spring.config.location=../application.properties &


echo "Starting Testrail collector"
cd ../../testrail-results-collector/target
nohup java -jar testrail-results-collector.jar  --spring.config.location=../application.properties &

echo '>> Currently Running Java Processes'
ps aux | grep java

sleep 10

echo "Done."


