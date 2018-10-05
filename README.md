# AOS_proj_2



**********************************
*** AOS - Project-2 : Points *****
**********************************



Configuration.java => parse config file, extract all required information and store them in appropriate variables
-- Testing pending



Server.java =>
Server class which opens at server port and waits for client to connect
Node 1 initiates the spanning tree construction
Use SCTP for communication
Details on SCTP @ https://www.ibm.com/developerworks/linux/library/l-sctp/?ca=dgr-lnxw07SCTP
-- partially done



RootParent.java =>
This class is used to store root, parent and ack count while performing broadcast operation
Generated Getter and Setter for each attribute used for accessing its value



Client.java => used to create connection with the server 
Used to send 'REQ_MSG' for creating spanning tree
and 'BROADCAST_MSG' to its corresponding children
-- Code pending




Node.java => This stores all node related information
-- code pending



Main.java =>
This is a main class
It take node id as argument
Calls function to parse config file
Each node starts its server which initiate spanning tree construction
Displays its neighboring nodes
Finally initiate broadcast operation for each node
-- code pending


