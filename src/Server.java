/*
********** AOS - Project 2 - Fall 2018 ******

Server class which opens at server port and waits for client to connect
Node 1 initiates the spanning tree construction*/

//using SCTP - Stream Control Transmission Protocol
// More details on SCTP @ https://www.ibm.com/developerworks/linux/library/l-sctp/?ca=dgr-lnxw07SCTP



// NOTE: Seperate class for Client, Node & Main would be needed ** Code pending **

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;

import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.ByteBuffer;


public class Server implements Runnable {
    private static final int MSG_SIZE = 1000; // Message size limited to 1000 characters
    private Charset charset = Charset.forName("ISO-8859-1"); //  ISO 8859-1 is a single-byte encoding
    private CharsetDecoder decoder = charset.newDecoder();
    private int serverPort=0, nodeId=0, root=0;
    private String hostName = null, send = null, rootTemp= null;
    private String pHost = null, msg = null, temp = null;
    private int pPort = 0, ackBC=0, senderId =0, parentId=0;
    

    public Server(int id){
        nodeId = id;
    }

    @Override
    public void run(){
        for (Map.Entry<Integer,Node> pair : Configuration.nodeHashMap.entrySet()){
            if (pair.getKey() == nodeId){
                serverPort = pair.getValue().getPort();
                hostName = pair.getValue().getHostName();
                System.out.println("Server: "+ hostName+" listening at "+serverPort);
                break;
            }
        }

        try{
            Client client = new Client(nodeId);
            //Open server channel and bind port to the channel
            SctpServerChannel serverChannel = SctpServerChannel.open();
            InetSocketAddress inetSockAddress = new InetSocketAddress(serverPort);
            serverChannel.bind(inetSockAddress);

            String recvMsg = null;
            ByteBuffer byteBufferRecv = ByteBuffer.allocate(MSG_SIZE);
            ByteBuffer byteBufferSend = ByteBuffer.allocate(MSG_SIZE);

            Charset charset = Charset.forName("ISO-8859-1");
            CharsetEncoder encoder = charset.newEncoder();

            Thread.sleep(1000);
            //For node 1 : Start client & initiate spanning tree proc
            //** Code Pending **
           

            while(true) {
                //Server starts accepting when the port is opened
                SctpChannel sctpChannel = serverChannel.accept();

                //Message will be received with binded information
                MessageInfo messageInfo = sctpChannel.receive(byteBufferRecv, null, null);

                byteBufferRecv.flip();
                if (byteBufferRecv.remaining() > 0) {  // actual length of received packet
                    recvMsg = decoder.decode(byteBufferRecv).toString();
                }
                byteBufferRecv.clear();
                CharBuffer cbuf = CharBuffer.allocate(100);
                RootParent rootParent = new RootParent();

                //Extract sender and root id from received msg
                send = new Scanner(recvMsg).useDelimiter("[^\\d]+").next();
                senderId = Integer.parseInt(send);
                recvMsg = recvMsg.substring(send.length());
                if( new Scanner(recvMsg).useDelimiter("[^\\d]+").hasNextInt()){
                    rootTemp = new Scanner(recvMsg).useDelimiter("[^\\d]+").next();
                    root = Integer.parseInt(rootTemp);
                }

                //This block handles spanning tree construction
                if (recvMsg != null){
                    if (recvMsg.equalsIgnoreCase("REQ_MSG")) {
                        // Spanning tree - ACK_ & NACK_MSG ** Code pending **
                    }

                    //Associates with the node as parent
                    //ACK_MSG refers to positive acknowledgment
                    else if (recvMsg.equalsIgnoreCase("ACK_MSG")) {
                        // Associates with the node as parent ** Code pending **

                    }

                    //If node has already been associated with parent
                    //NACK_MSG refers to Negative acknowledgement
                    else if (recvMsg.equalsIgnoreCase("NACK_MSG")) {
                        System.out.println("NACK message received from " + senderId+" to "+nodeId);
                    }

                    //This block begins Broadcast
                    else if (recvMsg.equalsIgnoreCase("BROADCAST_MSG")) {
                        System.out.println("\n"+"BROADCAST_MSG received at "+nodeId+" from "+senderId+" with root: "+ root);
                        //parentId = senderId;
                        rootParent.setParentId(senderId);
                        rootParent.setRoot(root);
                        Node.rootParentMap.put(root, rootParent);
                        if (Node.treeNeighbors.size() > 1) {
                            // ** Code Pending ** begin broadcast - call method in Client class
                        } else {
                            msg = nodeId + "BROADCAST_ACK" + rootParent.getRoot();
                            System.out.println("\n"+"BROADCAST_ACK sent from: "+nodeId+" to: "+rootParent.getParentId()+" with root: "+ root);
                            pHost = Configuration.hostNames.get(rootParent.getParentId() - 1);
                            pPort = Configuration.portList.get(rootParent.getParentId() - 1);
                            client.sendMessage(pHost, pPort, byteBufferSend, msg); // sendMessage should be in Client class
                        }
                    }

                    //This block handles Converge Cast
                    //Map used for handling concurrent broadcast operation
                    else if (recvMsg.equalsIgnoreCase("BROADCAST_ACK")) {
                        System.out.println("\n"+"BROADCAST_ACK received at: "+nodeId+" from: "+senderId+" with root: "+root);
                        for (Map.Entry<Integer,RootParent> pair : Node.rootParentMap.entrySet()){
                            if (root == pair.getKey()) {
                                parentId = pair.getValue().getParentId(); //Value = ParentId
                                ackBC = pair.getValue().getAck()+1;
                                pair.getValue().setAck(ackBC);
                                if (parentId == 0 && pair.getValue().getAck() == Node.treeNeighbors.size()) {
                                    if (Configuration.noOfBroadcast > 1) {
                                        Configuration.noOfBroadcast--;
                                        System.out.println("\n"+"Remaining broadcast :"+Configuration.noOfBroadcast+" for node:"+nodeId);
                                        RootParent rootParent1 = new RootParent();
                                        Node.rootParentMap.put(root,rootParent1);
                                        new Client(nodeId).beginBroadCast(nodeId, 0, root);

                                    } else {
                                        System.out.println("\n"+"Broadcast ended for Node: " + nodeId + "\n");
                                    }
                                } else if (parentId != 0 && pair.getValue().getAck() == Node.treeNeighbors.size() - 1) {
                                    //Send ack to its parent
                                    msg = nodeId + "BROADCAST_ACK" + root;
                                    System.out.println("\n"+"BROADCAST_ACK sent from: "+nodeId+" to: "+parentId+" with root: "+ root);
                                    pHost = Configuration.hostNames.get(parentId - 1);
                                    pPort = Configuration.portList.get(parentId - 1);
                                    client.sendMessage(pHost, pPort, byteBufferSend, msg); // sendMessage should be in Client class
                                }
                            }
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
