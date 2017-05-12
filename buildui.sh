cd UI
rm -rf dist/*
mvn clean install -DskipTests
cp -r dist/* /usr/share/nginx/html/
cat ../nginx.default > /etc/nginx/sites-enabled/default
sudo systemctl stop nginx
sudo systemctl start nginx
#service nginx reload
#systemctl stop nginx
#nginx
#systemctl status nginx
echo "Done"
