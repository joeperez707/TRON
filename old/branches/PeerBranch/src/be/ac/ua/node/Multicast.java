/**
 * 
 */
package be.ac.ua.node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import be.ac.ua.commands.XmlDecoder;
import be.ac.ua.commands.XmlEncoder;

/**
 * @author kris
 *
 */
public class Multicast implements Runnable{
	private int id;
	private int nextId;
	private int prevId;
	private int port;
	private String ip;
	private MulticastSocket mcsocket;
	private TreeMap<Integer, String> nodes;
	private static final String mcgroup = "225.4.5.6";
	private static final int mcport = 5000;
	private static final int maxvalue = 255;
	//private static final int maxvalue = Integer.MAX_VALUE;
	
	public Multicast (String ip, int port){
		this.port = port;
		this.ip = ip;
		this.id = 0;
		this.nextId = 0;
		this.prevId = 0;
		nodes = new TreeMap<Integer, String>();
		try {
			mcsocket = new MulticastSocket (mcport);
			mcsocket.setSoTimeout(10000);
			mcsocket.joinGroup(InetAddress.getByName(mcgroup));
		} catch (IOException e) {
			System.out.println("Problem joining multicast group");
			e.printStackTrace();
		}
	}
	
	public void run (){
		while (true) {
			try {
				byte buffer[] = XmlEncoder.XmlEncodeMulticastMessage(ip, port, this.getId()).getBytes();
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(mcgroup), mcport);
				mcsocket.send(packet);
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public void identify (){
		long end, begin = System.currentTimeMillis();
		end = begin;
		System.out.println("Listening for multicast messages from nodes on the network for 1 second...");
		while ((end - begin) < 1000) {
			try{
				byte buffer[] = new byte[mcsocket.getReceiveBufferSize()];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				mcsocket.receive(packet);
				nodes.put(XmlDecoder.XmlDecodeMulticastMessageId(new String(packet.getData())), XmlDecoder.XmlDecodeMulticastMessageAddress(new String(packet.getData())));
			} catch (SocketTimeoutException e1) {
				System.out.println("No multicast messages received");
			} catch (IOException e) {
				System.out.println("Problem identifying node in network");
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
		}
		assignId();
	}
	
	public void assignId(){
		if (nodes.size() >= 2){
			int next = 0, diff = 0;
			int previous = nodes.firstKey();
			for (Map.Entry<Integer, String> entry : nodes.entrySet())
			{
				next = entry.getKey();
				if ((next - previous) > diff){
					diff = next - previous;
					nextId = next;
					prevId = previous;
				}
				previous = next;
			}
			this.id = new Random(System.currentTimeMillis()).nextInt(nextId - prevId) + (prevId + 1); 
		} else if (nodes.size() == 1){
			nextId = nodes.firstKey();
			prevId = nextId;
			if (nextId > (maxvalue - nextId)){
				setId(new Random(System.currentTimeMillis()).nextInt(nextId));
			}
			else {
				setId(new Random(System.currentTimeMillis()).nextInt(maxvalue - nextId) + nextId);
			}
		} else {
			setId(new Random(System.currentTimeMillis()).nextInt(maxvalue));
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setPrevId(int id) {
		this.prevId = id;
	}

	public int getPrevId() {
		return prevId;
	}
	
	public void setNextId(int id) {
		this.nextId = id;
	}

	public int getNextId() {
		return nextId;
	}
	
	public String getPrevAddress() {
		if (nodes.containsKey(this.prevId)){
			return nodes.get(this.prevId);
		}
		else {
			return "255.255.255.255";
		}
	}
	
	public String getNextAddress() {
		if (nodes.containsKey(this.nextId)){
			return nodes.get(this.nextId);
		} 
		else {
			return "255.255.255.255";
		}
	}
	
	public String getAddress() {
		return this.ip;
	}
	
	
	
}
