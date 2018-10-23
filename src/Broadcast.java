import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.ArrayList;

public class Broadcast implements MsgListener, Broadcaster
{
	spanningTreeNode myNode;
	Integer parent;
	ArrayList<Integer> children;
	HashMap<Integer, Sender> senders;
	HashMap<Integer, Integer> counterHashMap;
	HashMap<Integer, Integer> immediateSourceHashMap;
	BlockingQueue<String> messages;
	int counterValue, max_counterValue, node;
	int terminatingNode;
	boolean terminated;
	boolean terminateReceived;

	public Broadcast(spanningTreeNode node)
	{
		immediateSourceHashMap = new HashMap<Integer, Integer>();
		counterHashMap = new HashMap<Integer, Integer>();
		messages = new LinkedBlockingDeque<String>();
		terminated = false;
		terminateReceived = false;
		terminatingNode = -1;
		myNode = node;
		parent = node.parent;
		children = node.children;
		senders = node.senders;
		// Maximum counter value is (neighbors-1)
		max_counterValue = (myNode.parent == myNode.nodeId) ? 0 : 1;
		max_counterValue += myNode.children.size() -1;
		counterHashMap.put(myNode.nodeId, max_counterValue + 1);
	}
	
	public String getNextMessage() throws InterruptedException
	{
		return messages.take();
	}

	@Override
	public synchronized boolean receive(StreamMsg m)
	{
		StreamMsg msg = new StreamMsg();
		msg.immediateSourceNodeId = myNode.nodeId;
		msg.sourceNodeId = m.sourceNodeId;
		msg.type = m.type;

		if(m.type== MsgType.broadcast || m.type == MsgType.broadcast_terminate)
		{			
			if(m.type == MsgType.broadcast_terminate)
			{
				System.out.println("Terminate received");
				terminateReceived = true;
				terminatingNode = m.sourceNodeId;
			}
			try {
				messages.put(m.message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			msg.message = m.message;
			// flood the message to its neighbors except m.sourceNodeId (immediate source)
			immediateSourceHashMap.put(m.sourceNodeId, m.immediateSourceNodeId);
			counterHashMap.put(m.sourceNodeId, 0);
			for(int i=0; i< children.size() ; i++)
			{
				node = children.get(i);
				if (node != m.immediateSourceNodeId)
				{
					// Send broadcast message -> call send(m)
					myNode.senders.get(node).send(msg);
				}
			}

			if(myNode.parent != m.immediateSourceNodeId)
			{
				if(myNode.parent != myNode.nodeId)
				{
					// Send broadcast message -> call send(m)
					myNode.senders.get(parent).send(msg);
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
			// If counter value reaches maximum send 'convergeCast_ack' message to it's immediate neighbors (i.e. ArrayList Children) 
			// and reset counter to 0
			counterValue = counterHashMap.get(m.sourceNodeId)+1;
			counterHashMap.put(m.sourceNodeId, counterValue);
			if(counterValue >= max_counterValue)
			{
				if(m.sourceNodeId == myNode.nodeId)
				{
					if(counterValue == max_counterValue + 1)
					{
						//System.out.println("Broadcast has been successful!");
						if(terminatingNode == m.sourceNodeId)
						{
							terminated = true;
						}
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
		return terminated;
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
		if(terminateReceived)
		{
			System.out.println("Cannot broadcast anymore. Terminate Received!");
			return;
		}	
		counterHashMap.put(myNode.nodeId, 0);
		if(m.type != MsgType.broadcast_terminate)
		{
			m.type = MsgType.broadcast;
		}
		else
		{
			terminatingNode = myNode.nodeId;
			terminateReceived = true;
		}
		m.sourceNodeId = myNode.nodeId;
		m.immediateSourceNodeId = myNode.nodeId;
		//To send message to node i, node.senders.get(i).send(m)
		for(int i=0; i< myNode.children.size() ; i++)
		{
			node = myNode.children.get(i);
			myNode.senders.get(node).send(m);
		}
		if(myNode.parent != myNode.nodeId)
		{
			myNode.senders.get(myNode.parent).send(m);
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

	void terminate()
	{
		StreamMsg m = new StreamMsg();
		m.type = MsgType.broadcast_terminate;
		broadcast(m);
	}

	void sendconvergeCast_ack(StreamMsg m)
	{
		if(m.sourceNodeId == terminatingNode)
		{
			System.out.println("Terminate acknowledged");
			terminated = true;
		}
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
		return terminated; 
	}
}

/* Logic for Broadcast

Immediate_source = HashMap => Key = Main_source, value = immediate_source

Maintain a counter C(i,j) - HashMap -  Key = Main_source, value = Count_of_convergeCast_ack_msg in node i for message source j
Each counter can have values from 0,1,2.... (neighbors-1)
The counter is incremented only for 'convergeCast_ack' message
When the counter reaches it's maximum value (i.e. neighbors-1) for Main_source then it sends convergeCast_ack message to it's Immediate_source node for the corresponding Main_source and resets counter C(i,j) to 0.
*/
