#!/bin/bash

mvn clean install -DskipTests
cd api
echo $1
echo $2
echo "Installing Nginx"
#apt-get install nginx -y
yum install epel-release -y
yum install nginx -y
service nginx start
echo "Stopping all java services"
kill `ps -eaf | grep java | awk {'print $2'}`
echo "Configuring API"
cp -f dashboard.template target/dashboard.properties
echo "dbhost="$2 >> target/dashboard.properties
cd target
java -jar api.jar --spring.config.location=./dashboard.properties > /dev/null 2>&1 &

echo "Configuring Jira collector"
cd ../../jira-feature-collector/
cp -f jira.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/23 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
java -jar jira-feature-collector.jar  > /dev/null 2>&1 &

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

echo "Starting UI"
cd ../../UI
cp -r dist/* /usr/share/nginx/html/
#cat ../nginx.default > /etc/nginx/sites-enabled/default
cat ../nginx.default > /etc/nginx/conf.d/api.conf
service nginx reload

sudo cat /var/log/audit/audit.log | grep nginx | grep denied | audit2allow -M mynginx
sudo semodule -i mynginx.pp

nginx -s reload

echo "Done..."
