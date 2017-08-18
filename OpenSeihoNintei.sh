#!/bin/bash

export _JAVA_OPTIONS='-Dawt.useSystemAAFontSettings=on'

cp /home/user/mnt/git/OpenSeihoCom/jpYmd.properties /home/user/mnt/git/OpenSeihoNintei/dist
cp /home/user/mnt/git/OpenSeihoCom/KEN_ALL.CSV /home/user/mnt/git/OpenSeihoNintei/dist
cd /home/user/mnt/git/OpenSeihoNintei/dist

java -jar OpenSeihoNintei.jar
