package be.ac.ua.com.client;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;


import be.ac.ua.com.Endpoint;
import be.ac.ua.node.Node;

public class ClientEndpoint extends Endpoint {

	private final static Logger LOG = Logger.getLogger(ClientEndpoint.class);
	
	public ClientEndpoint(Node node, int port) {
		super(node, port);
	}

	
	
	@Override
	public void run() {
		while (this.getState() > STARTED) {
			Socket controlsocket = null;
			Socket datasocket = null;
			LOG.info("Waiting for incoming connections");
			try {
				controlsocket = this.controlserversocket.accept();
				datasocket = this.dataserversocket.accept();
				LOG.debug("Creating a command handler for the commands coming in from the client");
				
			} catch (IOException e) {
				LOG.info("Socket has been closed");
				if (controlsocket != null) {
					try {
						controlsocket.close();
					} catch (IOException e1) {
						// ignore as socket is no longer needed
					}
				}
				controlsocket = null;
				if (datasocket != null) {
					try {
						datasocket.close();
					} catch (IOException e1) {
						// ignore as socket is no longer needed
					}
				}
				datasocket = null;
			}
		}
		
	}




}
