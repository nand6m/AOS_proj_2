import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

class Producer extends Thread{
	Broadcast b;
	private Scanner scanner;
	Producer(Broadcast broadcaster) { b = broadcaster; }
	public void run() {
		while (true) {
			scanner = new Scanner(System. in);
			String msg = scanner.nextLine();
			if(msg.equals("exit") || msg.equals("Exit"))
			{
				b.terminate();
			}
			else
			{
				b.broadcast(msg);
			}
		}
	}
}
