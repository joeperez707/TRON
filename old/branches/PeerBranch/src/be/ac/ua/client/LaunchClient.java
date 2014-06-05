package be.ac.ua.client;

import be.ac.ua.commands.*;

public class LaunchClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client();
		CLI cli = new CLI(client);
//		client.Join("192.168.0.45", 4002);
//		client.AddCommand(new Shutdown(165));
		Thread clientthread = new Thread(client);
		Thread commandthread = new Thread(cli);
		clientthread.start();
		commandthread.start();
	}
}
