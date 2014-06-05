package be.ac.ua.client;
//import java.io.File;
//
//import be.ac.ua.commands.Command;
//import be.ac.ua.commands.Get;
//import be.ac.ua.commands.LocalExecute;
//import be.ac.ua.commands.Put;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import be.ac.ua.commands.Command;
import be.ac.ua.commands.Get;
//import be.ac.ua.commands.LocalExecute;
import be.ac.ua.commands.Put;
import be.ac.ua.commands.XmlDecoder;
import be.ac.ua.node.ConnectionManager;



/**
 * @author Kris
 *
 */
public class Client implements Runnable{
	private ConnectionManager connectionmanager;
	private MulticastSocket mcsocket;
	private TreeMap<Integer, String> nodes;
	private LinkedBlockingQueue<Command> elements;
	private static final String mcgroup = "225.4.5.6";
	private static final int mcport = 5000;
	
	public Client (){
		connectionmanager = new ConnectionManager();
		nodes = new TreeMap<Integer, String>();
		this.elements = new LinkedBlockingQueue<Command>();
		try {
			mcsocket = new MulticastSocket (mcport);
			mcsocket.setSoTimeout(10000);
			//mcsocket.joinGroup(InetAddress.getByName(mcgroup));
		} catch (IOException e) {
			System.out.println("Problem joining multicast group");
			e.printStackTrace();
		}
		FindNodes();
	}
	
	public void FindNodes(){
		System.out.println("Listening for multicast messages from nodes on the network for 10 seconds...");
		this.nodes.clear();
		long end, begin = System.currentTimeMillis();
		end = begin;
		try {
			mcsocket.joinGroup(InetAddress.getByName(mcgroup));
			while ((end - begin) < 10000) {
				byte buffer[] = new byte[mcsocket.getReceiveBufferSize()];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				mcsocket.receive(packet);
				nodes.put(XmlDecoder.XmlDecodeMulticastMessageId(new String(packet.getData())), XmlDecoder.XmlDecodeMulticastMessageAddress(new String(packet.getData())));
				end = System.currentTimeMillis();
			}
			mcsocket.leaveGroup(InetAddress.getByName(mcgroup));
		} catch (SocketTimeoutException e1) {
			System.out.println("No multicast messages received");
		} catch (IOException e) {
			System.out.println("Problem finding nodes on network");
			e.printStackTrace();
		}
	}
	
	public void ListNodes(){
		if (nodes.isEmpty()){
			System.out.println("No nodes have been found on the network.");
		}
		else {
			System.out.println("The following nodes haven been found on the network:");
			for (Map.Entry<Integer, String> entry : nodes.entrySet())
			{
				System.out.println("Node: " + entry.getKey() + " ==> " + entry.getValue());
			}
		}
	}
	
	public void AddCommand(Command command){
		try {
			elements.put(command);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void Join (String host, int port){
		Leave();
		connectionmanager.RegisterConnection(host, port);
	}
	
	public void Leave (){
		connectionmanager.CloseConnection();
	}
	
	public void Execute (Command command){
		while (!connectionmanager.IsConnected());
		String xmlcmd = command.toXml();
		connectionmanager.SendCommand (xmlcmd);
		if (command instanceof Put) {
			System.out.println("Sending file: " + ((Put)command).getFilename());
			connectionmanager.SendFile(((Put)command).getFilename());
			String response = connectionmanager.ReceiveResponse();
			if (response.equalsIgnoreCase("READY")){
				System.out.println("File sent succesfully");
			}
		} else if (command instanceof Get) {
			String response = connectionmanager.ReceiveResponse();
			int filesize = Integer.parseInt(response);
			String filename = ((Get)command).getId();
			if (filesize > 0) {
				connectionmanager.ReceiveData(filename, filesize);
			}
			else {
				System.out.println("Error while receiving file: " + filename);
			}
			/*
		} else if (command instanceof LocalExecute){
			String response = connectionmanager.ReceiveResponse();
			if (response.equalsIgnoreCase("READY")){
				System.out.println("Command: " + ((LocalExecute)command).getOscmd() + " has been executed");
			}
			else {
				System.out.println("Error while executing command: " + ((LocalExecute)command).getOscmd());
			}
			*/
		} else {
			System.out.println("Cannot initiate command");
		}
	}

	@Override
	public void run() {
		while (true) {
			if (! elements.isEmpty()){
				try {
					this.Execute(elements.take());
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
