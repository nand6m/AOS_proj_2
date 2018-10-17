import java.io.IOException;

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
			System.out.println("Initiating Spanning Tree construction");
			stn.initiateConstruction();
		}
		MessageManager.joinAllThreads();
	}
}
