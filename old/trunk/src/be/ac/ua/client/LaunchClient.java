package be.ac.ua.client;

public class LaunchClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client();
		CLI cli = new CLI(client);
		Thread clientthread = new Thread(client);
		Thread commandthread = new Thread(cli);
		clientthread.start();
		commandthread.start();
	}
}
