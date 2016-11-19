#!/usr/bin/env bash

echo
echo "BUILD..."
echo

mvn clean package -DskipTests

echo
echo "DEPLOY..."
echo

scp target/moodic.jar xonix@ec2-54-218-119-31.us-west-2.compute.amazonaws.com:~/moodic

ssh xonix@ec2-54-218-119-31.us-west-2.compute.amazonaws.com "
sudo /etc/init.d/moodic restart
tail -f /var/log/moodic.log
"