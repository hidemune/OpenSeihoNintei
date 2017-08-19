#!/bin/bash
# このファイルをUSBメモリ上から実行するには、
# $ ls -la /dev/disk/by-uuid | grep sdb1
# でUUIDを調べ、 /etc/fstab を編集する必要があります。
# 例：
# # /dev/sdb1
# UUID=1CA8-82CE  /media/1CA8-82CE  vfat user,rw,noauto,exec,codepage=932,iocharset=utf8        0       0


#if [ $$ != `pgrep -fo $0`  ]; then
#	echo `basename $0` is already running.
#	exit 1
#fi

CURDIR=`dirname $0`
echo $CURDIR

cd $CURDIR

java -jar $CURDIR/javacard.jar $1
