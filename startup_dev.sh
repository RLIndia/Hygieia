#!/bin/bash

if [ "$6" == "nobuild" ]
then
    echo "No Build request found ... proceeding to restart of collectors"
else
    mvn clean install -DskipTests -Dpmd.skip=true
fi
cd api
echo $1
echo $2
echo $4
echo $5

#echo "Installing Nginx"
#apt-get install nginx -y
echo "Stopping all java services"


kill -9 $(ps -aux | grep java | grep api.jar | grep spring | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep octopus-deployment-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep jenkins-build-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep bitbucket-scm-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep github-scm-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep sbux-functional-test-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep jira-project-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep sonar-codequality-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep catalyst-deployment-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep testrail-results-collector.jar | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep chef-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')

echo "Configuring API"
cp -f dashboard.template target/dashboard.properties
echo "dbhost="$2 >> target/dashboard.properties
cd target
nohup java -jar api.jar --spring.config.location=./dashboard.properties &

echo "Fetching Token"
request="--post-data={\"username\":\"$3\",\"pass\":\"$4\",\"authType\":\"token\"} $1/auth/signin"
wget -S --header='Accept-Charset: UTF-8' --header='Content-Type: application/json' -O response.json $request
token=$(sed -e 's/^.*"token":"\([^"]*\)".*$/\1/' response.json)
echo $token
rm response.json



echo "Configuring Octopus collector"
cd ../../octopus-deployment-collector/
cp -f octopus.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5"  --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/28 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
nohup java -jar octopus-deployment-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Octopus Environment collector"
cd ../../octopus-deployment-all-collector/
cp -f octopus.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5"  --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/28 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
nohup java -jar octopus-deployment-all-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Jenkins collector"
cd ../../jenkins-build-collector/
cp -f jenkins.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5" --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/20 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
nohup java -jar jenkins-build-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Bitbucket collector"
cd ../../bitbucket-scm-collector/
cp -f bitbucket.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5" --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/27 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar bitbucket-scm-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Github collector"
cd ../../github-scm-collector/
cp -f github.template target/application.properties
#wget --header="Accept-Charset: UTF-8"--header="dashboard-host:$5"  --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/27 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar github-scm-collector-2.0.2-SNAPSHOT.jar &


echo "Configuring Jira Project collector"
cd ../../jira-project-collector/
cp -f jira.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5" --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/23 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar jira-project-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Functional Test collector"
cd ../../sbux-functional-test-collector/
cp -f application.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5" --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/29 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar sbux-functional-test-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Chef collector"
cd ../../chef-collector/
cp -f chef.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5" --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/10 -O target/temp.properties
chefId=$(sed -n 's/.*chef.id *= *\([^ ]*.*\)/\1/p' < target/temp.properties)
echo $chefId
wget --header="Accept-Charset: UTF-8"  --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/chef/pemFile/$chefId -O target/chef.pem

cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
echo "chef.pemFilePath="$(pwd)/target/chef.pem >> target/application.properties
cd target
#nohup java -jar chef-collector-2.0.2-SNAPSHOT.jar &

echo "Configuring Sonar collector"
cd ../../sonar-codequality-collector/
cp -f sonar.template target/application.properties
wget --header="Accept-Charset: UTF-8" --header="dashboard-host:$5" --header="x-catalyst-auth:\"$token\"" $1/d4dMastersCICD/readmasterjsonnew/31 -O target/temp.properties
cat target/temp.properties >> target/application.properties
echo "dbhost="$2 >> target/application.properties
cd target
#nohup java -jar sonar-codequality-collector-2.0.2-SNAPSHOT.jar &





echo "Starting UI"
cd ../../UI
cp -r dist/* /usr/share/nginx/html/
cat ../nginx.default > /etc/nginx/sites-enabled/default
service nginx stop
service nginx start
#nohup node/node node_modules/gulp/bin/gulp.js serve &
echo "Done..."

