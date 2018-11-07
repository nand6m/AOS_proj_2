import java.util.concurrent.ThreadLocalRandom;

public class BroadcastTester {
	
       Broadcast b;	
		public BroadcastTester(Broadcast b,Integer N) throws InterruptedException
		{		
			this.b = b;
			int msgCnt = 10;//#messages to be sent
			int max = 100;
			int sum = 0;
			
			for(Integer i = 0; i < msgCnt; i++) {
				int randomInt = ThreadLocalRandom.current().nextInt(max + 1);
				//System.out.println("Random number generated is : " + randomInt);
				String r = Integer.toString(randomInt);
			    b.broadcast(r);
			    sum += randomInt;
			}
			for(Integer k = 1; k < N; k++) {
				for(Integer i = 0; i < msgCnt; i++) {
					sum += Integer.parseInt(b.getNextMessage());
				}				
			}
			System.out.println("Total of all numbers sent & received = "+ sum);
		}
	
}
