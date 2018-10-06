import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.io.EOFException;

public class RunInThread extends Thread {
	Socket socket;
	NodeInfo NIobj;
	MsgListener l;

	public RunInThread(Socket socket,NodeInfo NIobj, MsgListener l) {
		this.socket=socket;
		this.NIobj=NIobj;	
		this.l = l;
		activeThreads.add(this);
	}

	public void run() {
		ObjectInputStream ois = null;
		boolean isRunning = true;
		//DataInputStream dis=null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			//System.out.println("Msg recvd");		
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		while(isRunning){
			try {			
				StreamMsg msg;
				msg=(StreamMsg) ois.readObject();

				if(msg.type == MsgType.terminate){
					System.out.println("Terminate received");
					isRunning = false;
					socket.close();
				}
				if(!l.receive(msg)){
					isRunning = false;
				}
				//System.out.println("neighbours msg recvd from "+socket.getRemoteSocketAddress().toString() +" "+ msg.phaseNeighbors);
				//System.out.println(NIobj.id+"says: "+msg.NodeId +" said "+msg.msg +" and");
				//System.out.println(msg.NodeId+ "'s neighbours are "+msg.neighbors);		
			}
			catch(SocketException se){
				if(l.isTerminated()){
					//if program has terminated gracefully then cleanup
					isRunning = false;
					try{
						socket.close();
					}
					catch(IOException ioe){
						ioe.printStackTrace();
					}
				}
				else{
					//else show the exception
					System.out.println("Exception from socket id: " + socket.getRemoteSocketAddress());
					se.printStackTrace();
					System.exit(2);
				}
			}
			catch(EOFException eofe){
				if(l.isTerminated()){
					//if program has terminated gracefully then cleanup
					isRunning = false;
					try{
						socket.close();
					}
					catch(IOException ioe){
						ioe.printStackTrace();
					}
				}
				else{
					//else show the exception
					System.out.println("Exception from socket id: " + socket.getRemoteSocketAddress());
					eofe.printStackTrace();
					System.exit(2);
				}
			}
			catch(StreamCorruptedException e) {
				System.out.println("Exception from socket id: " + socket.getRemoteSocketAddress());
				e.printStackTrace();
				System.exit(2);
			}
			catch (IOException e) {
				System.out.println("Exception from socket id: " + socket.getRemoteSocketAddress());
				e.printStackTrace();
				System.exit(2);
			}
			catch (ClassNotFoundException e) {
				System.out.println("Exception from socket id: " + socket.getRemoteSocketAddress());
				e.printStackTrace();
				System.exit(2);
			} 				
		}
		try{
			socket.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	static ArrayList<Thread> activeThreads = new ArrayList<Thread>();

	public static void joinAllThreads(){
		try{
			for(int i = 0; i < activeThreads.size(); i++){
				activeThreads.get(i).join();
			}
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
