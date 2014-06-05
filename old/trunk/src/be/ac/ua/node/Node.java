/**
 * 
 */
package be.ac.ua.node;

import java.util.concurrent.LinkedBlockingQueue;

import be.ac.ua.commands.Command;
import be.ac.ua.commands.XmlEncoder;

/**
 * @author kris
 *
 */
public class Node {
	private Master master;
	private Slave slave;
	private Multicast multicast;
	private ClientHandler clientHandler;
	private int id;
	private int nextid;
	private int previd;
	private String ip;
	private LinkedBlockingQueue<String> messagebus;
	
	public Node (String ip, int ListenerPort){
		this.ip = ip;
		this.messagebus = new LinkedBlockingQueue<String>();
		master = new Master(this);
		slave = new Slave(this, ListenerPort);
		clientHandler = new ClientHandler(this, ListenerPort);
		multicast = new Multicast(this.ip, ListenerPort);
		multicast.identify();
		setId(multicast.getId());
		System.out.println("This node's ID: " + this.id);
		System.out.println("This node's IP: " + this.ip);
		String address = multicast.getNextAddress();
		if (! address.equalsIgnoreCase("255.255.255.255")) {
			System.out.println("Entering ring network");
			nextid = multicast.getNextId();
			System.out.println("Next node's ID: " + nextid);
			System.out.println("Next node's IP: " + multicast.getNextAddress());
			master.Join(address, 4000);
			System.out.println("Sending connection update to the previous node");
			previd = multicast.getPrevId();
			System.out.println("Previous node's ID: " + previd);
			System.out.println("Previous node's IP: " + multicast.getPrevAddress());
			this.sendMessage(XmlEncoder.XmlEncodeConnectionUpdate(this.ip, previd, id, nextid));
		} else {
			nextid = 0; 
			previd = 0;
			System.out.println("First node on network");
		}
	}
	
	public void AddCommand (Command command){
		master.AddCommand(command);
	}
	
	public void Join (String host, int port){
		master.Join(host, port);
	}
	
	public void Leave () {
		master.Leave();
	}
	
	public void Start(){
		Thread masterThread = new Thread(this.master);
		//Thread slaveThread = new Thread(this.slave);
		Thread multicastThread = new Thread(this.multicast);
		Thread clientThread = new Thread(this.clientHandler);
		masterThread.start();
		multicastThread.start();
		clientThread.start();
		//slaveThread.start();
		slave.Start();
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setNextId(int nextid){
		this.nextid = nextid;
	}
	
	public int getNextId(){
		return nextid;
	}
	
	public void setPrevId(int previd){
		this.previd = previd;
	}
	
	public int getPrevId(){
		return previd;
	}
	
	public void sendMessage(String message) {
		try {
			messagebus.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String receiveMessage(){
		if (messagebus.isEmpty()){
			return null;
		}
		else {
			try {
				return messagebus.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
