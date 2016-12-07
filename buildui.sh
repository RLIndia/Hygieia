cd UI
mvn clean install -DskipTests
cp -r dist/* /usr/local/etc/nginx/html/
mkdir -p /usr/local/etc/nginx/sites-enabled
chmod 777 /usr/local/etc/nginx/sites-enabled
cat ../nginx_mac.default > /usr/local/etc/nginx/sites-enabled/default
sudo nginx -s stop
sudo nginx
