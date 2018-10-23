import java.util.concurrent.BlockingQueue;

class Consumer extends Thread {
   private final BlockingQueue<String> queue;
   Broadcast b;
   Consumer(BlockingQueue<String> q, Broadcast b) {
	   queue = q;
	   this.b = b;
	  }
   public void run() {
	   try {
		   while (true) {
			   String msg = queue.take();
			   if(msg.equals("exit") || msg.equals("Exit"))
			   {
				   b.terminate();
			   }
			   else
			   {
				   b.broadcast(msg);
			   }
		   }
	   } catch (InterruptedException ex) { 
		   System.out.println(ex);
	   }
   }
}
