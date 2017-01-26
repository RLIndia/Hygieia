#!/bin/bash
echo $1
echo $2
if [ "$3" == "nobuild" ]
then
    echo "No Build request found ... proceeding to restart of collectors"
else
    mvn clean install -DskipTests -Dpmd.skip=true
fi

cd api

echo "Installing Nginx"
#apt-get install nginx -y
echo "Stopping all java services"
pkill java

echo "Configuring API"
cp -f dashboard.template target/dashboard.properties
echo "dbhost="$2 >> target/dashboard.properties
cd target
nohup java -jar api.jar --spring.config.location=./dashboard.properties  &

echo "Configuring Octopus collector"
cd ../../octopus-deployment-collector/
cp -f octopus.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/28 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar octopus-deployment-collector-2.0.2-SNAPSHOT.jar &


echo "Configuring Octopus Environment collector"
cd ../../octopus-deployment-all-collector/
cp -f octopus.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/28 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
nohup java -jar octopus-deployment-all-collector-2.0.2-SNAPSHOT.jar &


echo "Configuring Jenkins collector"
cd ../../jenkins-build-collector/
cp -f jenkins.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/20 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
nohup java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Bitbucket collector"
cd ../../bitbucket-scm-collector/
cp -f bitbucket.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/27 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar bitbucket-scm-collector-2.0.2-SNAPSHOT.jar  &

echo "Configuring Functional Test collector"
cd ../../sbux-functional-test-collector/
cp -f application.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/29 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
nohup java -jar sbux-functional-test-collector-2.0.2-SNAPSHOT.jar &


echo "Configuring Jira collector"
cd ../../jira-feature-collector/
cp -f jira.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/23 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar jira-feature-collector.jar &


echo "Configuring Jira Project collector"
cd ../../jira-project-collector/
cp -f jira.template target/application.properties
wget $1/d4dMastersCICD/readmasterjsonnew/23 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar jira-project-collector-2.0.2-SNAPSHOT.jar &


echo "Configuring Sonar collector"
cd ../../sonar-codequality-collector/
cp -f sonar.template target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar sonar-codequality-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Catalyst collector"
cd ../../catalyst-deployment-collector/
cp -f catalyst.template target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar catalyst-deployment-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring TestRail collector"
cd ../../testrail-results-collector/
cp -f testrail.template target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar testrail-results-collector.jar &


echo "Starting UI"
cd ../../UI
cp -r dist/* /usr/local/etc/nginx/html/
mkdir -p /usr/local/etc/nginx/sites-enabled
chmod 777 /usr/local/etc/nginx/sites-enabled
cat ../nginx_mac.default > /usr/local/etc/nginx/sites-enabled/default
sudo nginx -s stop
sudo nginx
#nohup node/node node_modules/gulp/bin/gulp.js serve &
echo "Done..."
