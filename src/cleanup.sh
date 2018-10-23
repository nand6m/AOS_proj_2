#!/bin/bash

# Change this to your netid
netid=sxd167930

# Root directory of your project
DCDIR=/people/cs/s/$netid
RELDIR=broadcastService/AOS_proj_2
PROJDIR=$DCDIR/$RELDIR

# Directory where the config file is located on your local system
#CONFIGNAME=config_grade.txt
CONFIGNAME=config_20.txt
#CONFIGNAME=config_10nodes.txt
#CONFIGNAME=config.txt
CONFIGLOCAL=$HOME/Desktop/$RELDIR/src/$CONFIGNAME
CONFIGREMOTE=$PROJDIR/src/$CONFIGNAME

n=0

cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    while [[ $n -lt $i ]]
    do
    	read line
        host=$( echo $line | awk '{ print $1 }' )

        echo $host
        gnome-terminal -- ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host killall -u $netid &
        sleep 1

        n=$(( n + 1 ))
    done
   
)


echo "Cleanup complete"
