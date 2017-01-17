#!/bin/bash

export _JAVA_OPTIONS='-Dawt.useSystemAAFontSettings=on'

cp /home/user/git/OpenSeihoCom/jpYmd.properties /home/user/git/OpenSeihoNintei/dist
cp /home/user/git/OpenSeihoCom/KEN_ALL.CSV /home/user/git/OpenSeihoNintei/dist
cd /home/user/git/OpenSeihoNintei/dist

java -jar OpenSeihoNintei.jar
