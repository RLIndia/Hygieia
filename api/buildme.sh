mvn clean install -DskipTests -Dpmd.skip
cp dashboard.properties target/.
cd target
java -jar api.jar --spring.config.location=./dashboard.properties
