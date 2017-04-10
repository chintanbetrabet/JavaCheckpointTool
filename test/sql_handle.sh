v=10
sudo service mysql start

while [ $v -gt 0 ]
do
sleep 20
sudo service mysql stop
echo "STOPPED MYSQL"
sleep 20
sudo service mysql start
echo "RESTARTED MYSQL"
#v=$((v-1))
done
