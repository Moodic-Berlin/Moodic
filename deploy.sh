#!/usr/bin/env bash

USER=apps
SERV=ec2-54-218-119-31.us-west-2.compute.amazonaws.com

echo
echo "BUILD..."
echo

mvn clean package -DskipTests

echo
echo "DEPLOY..."
echo

scp moodic.conf target/moodic.jar $USER@$SERV:~/
#scp moodic.conf $USER@$SERV:~/

echo
echo "RESTART..."
echo

ssh $USER@$SERV "
if [ ! -f /etc/init.d/moodic ]
then
    sudo ln -s /home/apps/moodic.jar /etc/init.d/moodic
fi
sudo /etc/init.d/moodic restart
tail -f /var/log/moodic.log
"