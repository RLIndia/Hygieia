cd UI
mvn clean install -DskipTests
cp -r dist/* /usr/share/nginx/html/
cat ../nginx.default > /etc/nginx/sites-enabled/default
#service nginx reload
systemctl restart nginx
#systemctl status nginx
echo "Done"
