/**
 * 
 */
package be.ac.ua.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import be.ac.ua.commands.Command;
import be.ac.ua.commands.Update;

/**
 * @author kris
 *
 */
public class Peer {
	private class PeerHandler implements Runnable{
		private SocketInterface controlsocket;
		private SocketInterface datasocket;
		private boolean ClientHandler;
		
		public PeerHandler(Socket controlsocket, Socket datasocket, boolean ClientHandler){
			try {
				this.ClientHandler = ClientHandler;
				this.controlsocket = new NodeSocket(controlsocket);
				this.datasocket = new NodeSocket(datasocket);
			} catch (IOException e) {
				System.out.println("Could not connect to incoming connection");
				System.exit(1);
			}
		}
		@Override
		public void run() {
			NodeConnection prevnodeconnection = new NodeConnection(controlsocket, datasocket);
			if (this.ClientHandler) {
				Peer.this.setClient(prevnodeconnection);
			} else {
				Peer.this.setPrevNode(prevnodeconnection);
			}
			try {
				/* 
				 * this thread keeps listening for new commands until an IOException is raised
				 * due to the socket that has been closed on the other end, or until this node
				 * is shutdown.
				 */
				while (!Peer.this.shutdown) {
					Command command = prevnodeconnection.receiveCommand();
					command.setExecutionEnvironment(Peer.this, prevnodeconnection);
					Peer.this.AddCommand(command);
				}
			} catch (IOException e) {
				System.out.println("Connection has been closed by other end");
			}
			if (this.ClientHandler) {
				if (Peer.this.getClient().equals(prevnodeconnection)) {
					Peer.this.getClient().close();
				}
			} else {
				if (Peer.this.getPrevNode().equals(prevnodeconnection)) {
					Peer.this.getPrevNode().close();
				}
			}
			prevnodeconnection.close();
		}	
	}
	
	private class ClientHandler implements Runnable{
		private ServerSocket controlserversocket;
		private ServerSocket dataserversocket;
		
		public ClientHandler(){
			try {
				controlserversocket = new ServerSocket(4002);
				dataserversocket = new ServerSocket(4003);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			while (!shutdown){
				try {
					Socket controlsocket = controlserversocket.accept();
					Socket datasocket = dataserversocket.accept();
					new Thread(new PeerHandler(controlsocket, datasocket, true)).start();
				} catch (IOException e) {
					System.out.println("Problem accepting incoming client connections");
				}
			}
			try {
				controlserversocket.close();
				dataserversocket.close();
			} catch (IOException e) {
				System.out.println("Problem closing server sockets");
			}
		}
	}
	
	private class CommandHandler implements Runnable{
		
		@Override
		public void run() {
			while (true) { // vervangen door zolang shutdown niet gegeven is
				if (! commandqueue.isEmpty()){
					try {
						commandqueue.take().Execute();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}	
	}
	
	private boolean shutdown;
	private Multicast multicast;
	private ServerSocket controlserversocket;
	private ServerSocket dataserversocket;
	private NodeConnection nextnodeconnection;
	private NodeConnection prevnodeconnection;
	private NodeConnection clientconnection;
	private LinkedBlockingQueue<Command> commandqueue;
	
	public Peer(String ip, int ListenerPort){
		this.commandqueue = new LinkedBlockingQueue<Command>();
		shutdown = false;
		multicast = new Multicast(ip, ListenerPort);
		multicast.identify();
		try {
			controlserversocket = new ServerSocket(ListenerPort);
			dataserversocket = new ServerSocket(ListenerPort + 1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		String host = multicast.getNextAddress();
		if (host.equalsIgnoreCase("255.255.255.255")){
			System.out.println("First node in network");
		}
		else {
			this.Join(multicast.getNextAddress(), 4000);		
			nextnodeconnection.sendCommand(new Update(multicast.getAddress(), multicast.getPrevId(), multicast.getId(), multicast.getNextId()));
		}
	}
	
	public void AddCommand(Command command){
		try {
			commandqueue.put(command);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void Join(String host, int port){
		this.Leave();
		try {
			nextnodeconnection = new NodeConnection(host, port);
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve host");
		} catch (IOException e) {
			System.out.println("Problem connecting to next node");
		}
	}
	
	public void Leave(){
		if (nextnodeconnection != null){
			nextnodeconnection.close();
		}
	}
	
	public void MainLoop(){
		new Thread(multicast).start();
		new Thread(new CommandHandler()).start();
		new Thread(new ClientHandler()).start();
		while (!shutdown){
			try {
				Socket controlsocket = controlserversocket.accept();
				Socket datasocket = dataserversocket.accept();
				new Thread(new PeerHandler(controlsocket, datasocket, false)).start();
			} catch (IOException e) {
				System.out.println("Problem accepting incoming connections");
			}
		}
		//this node is shutting down
		//sending shutdown notification message to other nodes
		try {
			controlserversocket.close();
			dataserversocket.close();
		} catch (IOException e) {
			System.out.println("Problem closing server sockets");
		}
	}
	
	public void setShutdown(boolean status){
		this.shutdown = status;
	}
	
	public Multicast getMulticast(){
		return this.multicast;
	}
	
	public NodeConnection getNextNode(){
		return this.nextnodeconnection;
	}
	
	public void setNextNode(NodeConnection nextnodeconnection){
		this.nextnodeconnection = nextnodeconnection;
	}

	public void setPrevNode(NodeConnection prevnodeconnection) {
		this.prevnodeconnection = prevnodeconnection;
	}

	public NodeConnection getPrevNode() {
		return prevnodeconnection;
	}

	public NodeConnection getClient() {
		return clientconnection;
	}

	public void setClient(NodeConnection clientconnection) {
		this.clientconnection = clientconnection;
	}
	
}
