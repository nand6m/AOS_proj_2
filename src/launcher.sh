./cleanup.sh
#!/bin/bash

# Change this to your netid
netid=sxd167930

# Root directory of your project
DCDIR=/people/cs/s/$netid
RELDIR=broadcastService/AOS_proj_2
PROJDIR=$DCDIR/$RELDIR

# Directory where the config file is located on your local system
#CONFIGNAME=config_grade.txt
#CONFIGNAME=config_20.txt
#CONFIGNAME=config_10nodes.txt
CONFIGNAME=config.txt
CONFIGLOCAL=$HOME/Desktop/$RELDIR/src/$CONFIGNAME
CONFIGREMOTE=$PROJDIR/src/$CONFIGNAME

# Directory your java classes are in
BINDIR=$PROJDIR/bin

# Your main project class
PROG=Main

#Compile
javac *.java
mv *.class ../bin -f
echo 'Compiled'

#Move
scp *.java $netid@dc01.utdallas.edu:$RELDIR/src/
scp *.txt $netid@dc01.utdallas.edu:$RELDIR/src/
ssh $netid@dc01.utdallas.edu "./$RELDIR/src/Compile.sh"
echo 'Deployment complete'

n=0

cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    p=0
    while [[ $n -lt $i ]]
    do
    	read line
	p=$(( p + 1 ))
        host=$( echo $line | awk '{ print $1 }' )
	
	gnome-terminal -- bash -c "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $p $CONFIGREMOTE; exec bash" &

        n=$(( n + 1 ))
    done
)
