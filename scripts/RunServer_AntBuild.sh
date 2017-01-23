#!/bin/sh

cd db

java -DDOMEROOT=../../ -Djava.library.path=/home/beckmann/software/DOME/ceed/trunk/Dome/dlls  -jar ../../dist/server/ceed_server-20110902.jar
