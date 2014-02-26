#!/bin/bash

export _JAVA_OPTIONS='-Dawt.useSystemAAFontSettings=on'

cp /home/hdm/NetBeansProjects/OpenSeihoCom/jpYmd.properties /home/hdm/NetBeansProjects/OpenSeihoNintei/dist
cp /home/hdm/NetBeansProjects/OpenSeihoCom/KEN_ALL.CSV /home/hdm/NetBeansProjects/OpenSeihoNintei/dist
cd /home/hdm/NetBeansProjects/OpenSeihoNintei/dist

java -jar OpenSeihoNintei.jar
