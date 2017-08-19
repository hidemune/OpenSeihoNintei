#!/bin/bash

export _JAVA_OPTIONS='-Dawt.useSystemAAFontSettings=on'

cd /home/user/mnt/git/OpenSeihoNintei/
cp ../OpenSeihoCom/jpYmd.properties dist
cp ../OpenSeihoCom/KEN_ALL.CSV dist
cp -r OS_NinteiHelp dist
cd /home/user/mnt/git/OpenSeihoNintei/

java -jar dist/OpenSeihoNintei.jar
