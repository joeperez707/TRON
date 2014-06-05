package be.ac.ua.com;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import be.ac.ua.commands.Get;
import be.ac.ua.node.Node;

public abstract class Endpoint implements Runnable{
	public static final int STARTED = 0;
	public static final int LISTENING = 1;
	public static final int ACCEPTING = 2;
	public static final int DISCONNECTING = 3;
	
	protected final static Logger LOG = Logger.getLogger(Endpoint.class);
	protected ServerSocket controlserversocket;
	protected ServerSocket dataserversocket;
	protected Node node;
	
	protected int ListenerPort;
	private int State = -1;
	
	protected Endpoint(Node node, int port){
		this.node = node;
		this.ListenerPort = port;
	}

	protected void setState(int state) {
		State = state;
	}

	protected int getState() {
		return State;
	}
	
	public void OpenConnection() {
		try {
			this.controlserversocket = new ServerSocket(this.ListenerPort);
			this.dataserversocket = new ServerSocket(this.ListenerPort + 1);
			this.setState(LISTENING);
			LOG.info("Sockets have been opened for listening");
			Thread listenerThread = new Thread(this, "Endpoint_" +  this.ListenerPort + "_Thread");
			listenerThread.start();
		} catch (IOException e) {
			LOG.error("Endpoint could not listen on port " + this.ListenerPort + " " + e.getMessage());
			throw new RuntimeException("SocketEndpoint could not listen on port "
							+ this.ListenerPort + " " + e.getMessage());
		}
	}
}
