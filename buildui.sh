cd UI
mvn clean install -DskipTests
rm -rf /usr/share/nginx/html/*
cp  -f -r dist/* /usr/share/nginx/html/
cat ../nginx.default > /etc/nginx/sites-enabled/default
service nginx reload

