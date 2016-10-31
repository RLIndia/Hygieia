#!/bin/bash
kill -9 $(ps -aux | grep java | grep api.jar | grep spring | awk '{print $2}')
kill -9 $(ps -aux | grep java | grep octopus-deployment-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep jenkins-build-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep  bitbucket-scm-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep  bitbucket-scm-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep  sbux-functional-test-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep  jira-project-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep  sonar-codequality-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep  catalyst-deployment-collector-2.0.2-SNAPSHOT.jar | awk '{print $2}')
pkill -9 $(ps -aux | grep java | grep  testrail-results-collector.jar | awk '{print $2}')

