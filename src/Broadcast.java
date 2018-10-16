import java.util.HashMap; 

public class Broadcast implements MsgListener, Broadcaster
{
	spanningTreeNode myNode;
	HashMap<Integer, Integer> counterHashMap = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer> immediateSourceHashMap = new HashMap<Integer, Integer>();
	int counterValue, max_counterValue, node;

	public Broadcast(spanningTreeNode node)
	{
		myNode = node;
	}

	@Override
	public boolean receive(StreamMsg m)
	{
		//To send message to node i, node.senders.get(i).send(m)
		StreamMsg msg = new StreamMsg();
		msg.immediateSourceNodeId = myNode.nodeId;
		msg.sourceNodeId = m.sourceNodeId;
		msg.type = m.type;

		if(m.type== MsgType.broadcast)
		{			
			// flood the message to its neighbours except m.sourceNodeId (immediate source)
			// Looping through 'Children' 

			immediateSourceHashMap.put(m.sourceNodeId, m.immediateSourceNodeId);
			for(int i=0; i< children.size() ; i++)
			{
				node = children.get(i);
				if (node != m.immediateSourceNodeId)
				{
					//message = nodeID + "BROADCAST_MSG";
					// Send broadcast message -> call send(m)
					myNode.senders.get(node).send(msg);
					System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
				}
			}

			if(myNode.parent != m.immediateSourceNodeId)
			{
				if(myNode.parent != myNode.nodeId)
				{
					//message = nodeID + "BROADCAST_MSG";
					// Send broadcast message -> call send(m)
					myNode.senders.get(parent).send(msg);
					System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
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
			max_counterValue = (myNode.parent == myNode.nodeId) ? 0:1;
			max_counterValue += myNode.children.size() -1;
			if(counterValue == max_counterValue)
			{
				counterHashMap.put(m.sourceNodeId, 0);
				sendconvergeCast_ack(msg);
			}
		}
		//Return any value for now
		return false;
	}

	//This function will be called from outside. Do not call this function in this class
	@Override
	public void broadcast(StreamMsg m)
	{
		//To send message to node i, node.senders.get(i).send(m)
		for(int i=0; i< myNode.children.size() ; i++)
		{
			node = myNode.children.get(i);
			//message = nodeID + "BROADCAST_MSG";
			// Send broadcast message -> call send(m)
			myNode.senders.get(node).send(m);
			System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
		}
		if(myNode.parent != myNode.nodeId)
		{
			//message = nodeID + "BROADCAST_MSG";
			// Send broadcast message -> call send(m)
			myNode.senders.get(myNode.parent).send(m);
			System.out.println("\n"+"Broadcast Message sent from " + myNode + " to " + node);
		}
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
	public boolean isTerminated()
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
