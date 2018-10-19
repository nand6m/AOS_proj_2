import java.util.HashMap; 
import java.util.ArrayList;

public class Broadcast implements MsgListener, Broadcaster
{
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_CYAN = "\u001B[36m";

	spanningTreeNode myNode;
	Integer parent;
	ArrayList<Integer> children;
	HashMap<Integer, Sender> senders;
	HashMap<Integer, Integer> counterHashMap = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer> immediateSourceHashMap = new HashMap<Integer, Integer>();
	int counterValue, max_counterValue, node;

	public Broadcast(spanningTreeNode node)
	{
		myNode = node;
		parent = node.parent;
		children = node.children;
		senders = node.senders;
		max_counterValue = (myNode.parent == myNode.nodeId) ? 0:1;
		max_counterValue += myNode.children.size() -1;
		counterHashMap.put(myNode.nodeId, max_counterValue + 1);
	}

	@Override
	public synchronized boolean receive(StreamMsg m)
	{
		//To send message to node i, node.senders.get(i).send(m)
		StreamMsg msg = new StreamMsg();
		msg.immediateSourceNodeId = myNode.nodeId;
		msg.sourceNodeId = m.sourceNodeId;
		msg.type = m.type;

		if(m.type== MsgType.broadcast)
		{			
			System.out.println(ANSI_CYAN + m.message + ANSI_RESET);
			msg.message = m.message;
			// flood the message to its neighbours except m.sourceNodeId (immediate source)
			// Looping through 'Children' 

			immediateSourceHashMap.put(m.sourceNodeId, m.immediateSourceNodeId);
			counterHashMap.put(m.sourceNodeId, 0);
			for(int i=0; i< children.size() ; i++)
			{
				node = children.get(i);
				if (node != m.immediateSourceNodeId)
				{
					//message = nodeID + "BROADCAST_MSG";
					// Send broadcast message -> call send(m)
					myNode.senders.get(node).send(msg);
					//System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
				}
			}

			if(myNode.parent != m.immediateSourceNodeId)
			{
				if(myNode.parent != myNode.nodeId)
				{
					//message = nodeID + "BROADCAST_MSG";
					// Send broadcast message -> call send(m)
					myNode.senders.get(parent).send(msg);
					//System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
				}
			}

			if(myNode.children.size() == 0)
			{
				sendconvergeCast_ack(msg);
			}
		}
		else if(m.type== MsgType.convergeCast_ack)
		{
			// Acknowledgement received -> Increment counter value in counterHashMap where Key = m.sourceNodeId (Main source)
			// If counter value reaches maximum send 'convergeCast_ack' message to it's immediate neighbours (i.e. ArrayList Children) 
			// and reset counter to 0
			counterValue = counterHashMap.get(m.sourceNodeId)+1;
			counterHashMap.put(m.sourceNodeId, counterValue);
			// Maximum counter value is (neighbours-1)
			if(counterValue >= max_counterValue)
			{
				if(m.sourceNodeId == myNode.nodeId)
				{
					if(counterValue == max_counterValue + 1)
					{
						//System.out.println("Broadcast has been successful!");
						notify();
					}
				}
				else
				{
					counterHashMap.put(m.sourceNodeId, 0);
					sendconvergeCast_ack(msg);
				}
			}
		}
		//Return any value for now
		return false;
	}

	//This function will be called from outside. Do not call this function in this class
	@Override
	public synchronized void broadcast(StreamMsg m)
	{
		while(counterHashMap.get(myNode.nodeId) != max_counterValue + 1)
		{
			try{
				wait();
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
				return;
			}
		}	
		counterHashMap.put(myNode.nodeId, 0);
		m.type = MsgType.broadcast;
		m.sourceNodeId = myNode.nodeId;
		m.immediateSourceNodeId = myNode.nodeId;
		//To send message to node i, node.senders.get(i).send(m)
		for(int i=0; i< myNode.children.size() ; i++)
		{
			node = myNode.children.get(i);
			//message = nodeID + "BROADCAST_MSG";
			// Send broadcast message -> call send(m)
			myNode.senders.get(node).send(m);
			//System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
		}
		if(myNode.parent != myNode.nodeId)
		{
			//message = nodeID + "BROADCAST_MSG";
			// Send broadcast message -> call send(m)
			myNode.senders.get(myNode.parent).send(m);
			//System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
		}
	}
	
	public synchronized void broadcast(String s) {
		StreamMsg m = new StreamMsg();
		m.message = s;	
		m.type = MsgType.broadcast;
		broadcast(m);
	}
	
	void sendOkay(StreamMsg m)
	{
		m.type = MsgType.okay;
		m.message = "";
		m.immediateSourceNodeId = myNode.nodeId;
		int immediate_source = immediateSourceHashMap.get(m.sourceNodeId);
		myNode.senders.get(immediate_source).send(m);
		immediateSourceHashMap.remove(m.sourceNodeId);
	}

	void sendconvergeCast_ack(StreamMsg m)
	{
		m.type = MsgType.convergeCast_ack;
		m.message = "";
		m.immediateSourceNodeId = myNode.nodeId;
		int immediate_source = immediateSourceHashMap.get(m.sourceNodeId);
		myNode.senders.get(immediate_source).send(m);
		immediateSourceHashMap.remove(m.sourceNodeId);
	}

	@Override
	public synchronized boolean isTerminated()
	{
		//No need to modify
		return false;
	}
}

/* Logic for Broadcast

Immediate_source = HashMap => Key = Main_source, value = immediate_source

Maintain a counter C(i,j) - HashMap -  Key = Main_source, value = Count_of_convergeCast_ack_msg in node i for message source j
Each counter can have values from 0,1,2.... (neighbours-1)
The counter is incremented only for 'convergeCast_ack' message
When the counter reaches it's maximum value (i.e. neighbours-1) for Main_source then it sends convergeCast_ack message to it's Immediate_source node for the corresponding Main_source and resets counter C(i,j) to 0.
*/
