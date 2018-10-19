import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Main {
	static NodeInfo NIobj=new NodeInfo();

	public static void main(String[] args) throws IOException, InterruptedException {	
		NIobj = ReadConfigFile.readConfigFile(args[1]);
		NIobj.id = Integer.parseInt(args[0]);
		for(int i=0;i<NIobj.nodes.size();i++){
			NIobj.nodeInfo.put(NIobj.nodes.get(i).nodeId, NIobj.nodes.get(i));
		}
		
		spanningTreeNode stn = new spanningTreeNode(NIobj.id);
		MessageManager.setSpanningTreeNode(stn);
		 
		TCPServer server = new TCPServer(NIobj);		
		TCPClient client = new TCPClient(NIobj, NIobj.id);
		server.listenforinput();
		System.out.println("All connections done!");
		if(NIobj.id == 1){
			Thread.sleep(2000);
			System.out.println("Initiating Spanning Tree construction");
			stn.initiateConstruction();
		}
		System.out.println(stn.isTerminated());
		while(!stn.isTerminated());
		Broadcast b = new Broadcast(stn);
		MessageManager.setBroadcast(b);
		Thread.sleep(2000);

		//StreamMsg m = new StreamMsg();
		//m.message = "Message from node " + NIobj.id;
		//System.out.println("Sending/Broadcasting message: " + m.message);
		//b.broadcast(m);
		
		
		BlockingQueue<String> q = new LinkedBlockingDeque<String>();
		//Producer reads std I/O and puts in queue
		Producer p = new Producer(q);
		//Consumer reads from queue and does broadcast
	     	Consumer c = new Consumer(q,b);
	     
	     	p.start();
	    	c.start();
		p.join();
		c.join();
		MessageManager.joinAllThreads();
	}
}
