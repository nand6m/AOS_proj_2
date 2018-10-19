import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

class Producer extends Thread{
	   private final BlockingQueue<String> queue;
	private Scanner scanner;
	   Producer(BlockingQueue<String> q) { queue = q; }
	   public void run() {
	     try {
	       while (true) {
	    	   scanner = new Scanner(System. in);
	    	   String msg = scanner.nextLine();
	    	   queue.put(msg); }
	     } catch (InterruptedException ex) { 
	    	 System.out.println(ex);
	     }
	   }
	  // Object produce() { ... }
	 }
