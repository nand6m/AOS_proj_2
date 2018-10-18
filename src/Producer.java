import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

class Producer implements Runnable {
	   private final BlockingQueue queue;
	   Producer(BlockingQueue q) { queue = q; }
	   public void run() {
	     try {
	       while (true) {
	    	   Scanner scanner = new Scanner(System. in);
	    	   String msg = scanner. nextLine();
	    	   queue.put(msg); }
	     } catch (InterruptedException ex) { 
	    	 System.out.println(ex);
	     }
	   }
	  // Object produce() { ... }
	 }
