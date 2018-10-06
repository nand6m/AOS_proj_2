public class Broadcast implements MsgListener, Broadcaster
{
	spanningTreeNode myNode;
	public Broadcast(spanningTreeNode node)
	{
		myNode = node;
	}

	boolean receive(StreamMsg m)
	{
		//To send message to node i, node.senders.get(i).send(m)

		//Return any value for now
		return false;
	}

	//This function will be called from outside. Do not call this function in this class
	void broadcast(StreamMsg m)
	{
		//To send message to node i, node.senders.get(i).send(m)
	}

	boolean isTerminated()
	{
		//No need to modify
		return false;
	}
}
