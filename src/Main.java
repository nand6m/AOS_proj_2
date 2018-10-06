import java.io.IOException;

public class Main {
	static NodeInfo NIobj=new NodeInfo();

	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		NIobj =ReadConfigFile.readConfigFile(args[1]);
		System.out.println(Integer.parseInt(args[0]));
		NIobj.id =Integer.parseInt(args[0]);
		 
		spanningTreeNode stn=new spanningTreeNode(NIobj.id);
		//BroadCast b= new BroadCast();
		//kNeighbor kn=new kNeighbor(NIobj.id,NIobj.ClientConnectionCount[NIobj.id],b);
		System.out.println("Entered");
		
		for(int i=0;i<NIobj.nodes.size();i++){
			NIobj.nodeInfo.put(NIobj.nodes.get(i).nodeId, NIobj.nodes.get(i));
		}
		
		 
		TCPServer server = new TCPServer(NIobj);		
		TCPClient client = new TCPClient(NIobj, NIobj.id,kn);
		server.listenforinput(kn);
	}
}
