class Consumer extends Thread {
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_CYAN = "\u001B[36m";
	Broadcast b;
	Consumer(Broadcast b) {
		this.b = b;
	}
	public void run() {
		try {
			while (true) {
				String msg = b.getNextMessage();
				System.out.println(ANSI_CYAN + msg + ANSI_RESET);
			}
		} catch (InterruptedException ex) { 
			System.out.println(ex);
		}
	}
}
