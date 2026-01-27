#!/bin/bash
mvn clean package
cd target
echo "Stopping SchengenDays service"
sudo systemctl stop schengendays
rm /home/jonathan/SchengenDays/*.log.*.gz
cp schengen-days-0.0.1-SNAPSHOT.jar /home/jonathan/SchengenDays/SchengenDays.jar
echo "Starting SchengenDays service"
sudo systemctl start schengendays


# cd /home/jonathan/videoDownloader
# sudo java -jar -Xms1024M -Xmx4096M SpringVideoDownloader-0.0.1-SNAPSHOT.jar
