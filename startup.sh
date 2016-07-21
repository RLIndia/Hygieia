#!/bin/bash

/srv/users/opscode/apache-maven-3.3.9/bin/mvn clean install -DskipTests
cd api
echo $1
echo $2
#echo "Installing Nginx"
#apt-get install nginx -y
echo "Stopping all java services"
pkill java
echo "Configuring API"
cp -f dashboard.template target/dashboard.properties
echo "dbhost="$2 >> target/dashboard.properties
cd target
java -jar api.jar --spring.config.location=./dashboard.properties > /dev/null 2>&1 &

echo "Configuring Octopus collector"
cd ../../octopus-deployment-collector/
cp -f octopus.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/28 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
java -jar octopus-deployment-collector-2.0.2-SNAPSHOT.jar > /dev/null 2>&1 &

echo "Configuring Jenkins collector"
cd ../../jenkins-build-collector/
cp -f jenkins.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/20 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar > /dev/null 2>&1 &

echo "Configuring Bitbucket collector"
cd ../../bitbucket-scm-collector/
cp -f bitbucket.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/27 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
java -jar bitbucket-scm-collector-2.0.2-SNAPSHOT.jar > /dev/null 2>&1 &


<<'COMMENT'

echo "Configuring Jira collector"
cd ../../jira-feature-collector/
cp -f jira.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/23 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
java -jar jira-feature-collector.jar  > /dev/null 2>&1 &



echo "Configuring Sonar collector"
cd ../../sonar-codequality-collector/
cp -f sonar.template target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
java -jar sonar-codequality-collector-2.0.2-SNAPSHOT.jar > /dev/null 2>&1 &

COMMENT
 
echo "Starting UI"
cd ../../UI
#cp -r dist/* /usr/share/nginx/html/
#cat ../nginx.default > /etc/nginx/sites-enabled/default
#service nginx reload
nohup node/node node_modules/gulp/bin/gulp.js serve &
echo "Done..."
