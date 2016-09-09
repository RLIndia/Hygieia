#!/bin/bash
mvn clean install -DskipTests  -Dpmd.skip=true
cp application.properties target/.
