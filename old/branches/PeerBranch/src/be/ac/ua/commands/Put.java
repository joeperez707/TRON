package be.ac.ua.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import be.ac.ua.node.CommSocket;
import be.ac.ua.node.Multicast;
import be.ac.ua.node.NodeConnection;
import be.ac.ua.node.Peer;


/**
 * 
 */

/**
 * @author uauser
 *
 */
public class Put implements Command{
	private String filename;
	private int hash;
	private int datalength;
	private byte[] databuffer;
	private CommSocket datasocket;
	private CommSocket controlsocket;
	private boolean storedlocally;
	private static final int maxvalue = 255;
	private Peer node;
	private NodeConnection prevNode;
	//private static final int maxvalue = Integer.MAX_VALUE;
	
	public Put(String filename, int datalength){
		this.setFilename(filename);
		this.setDataSocket(null);
		this.setControlSocket(null);
		this.setDatalength(datalength);
		this.databuffer = new byte[this.datalength];
		this.setHash((Math.abs(filename.hashCode()))%maxvalue);
		this.setStoredlocally(false);
	}
	
	public Put(String filename) {
		this.setFilename(filename);
		this.setDataSocket(null);
		this.setControlSocket(null);
		this.setHash((Math.abs(filename.hashCode()))%maxvalue);
		this.setStoredlocally(false);
		File file = new File(filename);
		this.setDatalength((int)file.length());
		this.databuffer = new byte[this.datalength];
	}
	
	public void Execute (){
		Multicast multicast = node.getMulticast();
		if (multicast.getId() <= this.getHash()){
			if ((multicast.getId() < multicast.getPrevId()) && (multicast.getPrevId() <= this.getHash())){
				this.setStoredlocally(true);
			} else {
				node.getNextNode().sendCommand(this);
				this.setStoredlocally(false);
			}
		}
		else {
			if (multicast.getPrevId() <= (this.getHash())){
				this.setStoredlocally(true);
			} else {
				if (multicast.getPrevId() > multicast.getId()){
					this.setStoredlocally(true);
				} else {
					node.getNextNode().sendCommand(this);
					this.setStoredlocally(false);
				}
			}
		}
		TransmitData();
	}
	
	/**
	 * Moving file data from the previous node to the current node,
	 * either storing it locally or relaying it to the next.
	 */
	public void TransmitData() {
		if (isStoredlocally()){
			System.out.println("Storing file locally");
			try {
				FileOutputStream fos = new FileOutputStream(this.filename);
				int buffer;
				while ((buffer = prevNode.read()) > -1){
					fos.write(buffer);
				}
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			} catch (IOException e) {
				System.out.println("Problem storing file locally");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			node.getNextNode().sendCommand("READY\n");
		}
		else {
			System.out.println("Forwarding file to next node");
			try {
				node.getNextNode().pipeConnection(prevNode);
			} catch (IOException e) {
				System.out.println("Problem forwarding file");
				e.printStackTrace();
			}
		}
	}

	public String toXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>" + this.getCommandType() + "</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<filename>" + this.getFilename() + "</filename>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<filesize>" + this.getDatalength() + "</filesize>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<hash>" + this.getHash() + "</hash>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append("\n");
		String xmlcmd = sb.toString();
		return xmlcmd;
	}

	public Command fromXml(String xml) {
		return XmlDecoder.XmlDecode(xml);
	}
	
	public void setHash(int hash) {
		this.hash = hash;
	}
	
	public int getHash() {
		return this.hash;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public String getCommandType() {
		return "Put";
	}

	public void setDataSocket(CommSocket datasocket) {
		this.datasocket = datasocket;
	}

	public CommSocket getDataSocket() {
		return datasocket;
	}

	public void setControlSocket(CommSocket controlsocket) {
		this.controlsocket = controlsocket;
	}

	public CommSocket getControlSocket() {
		return controlsocket;
	}

	public void setDatalength(int datalength) {
		this.datalength = datalength;
	}

	public int getDatalength() {
		return datalength;
	}

	/**
	 * @param set to true if the file is to be stored on this node,
	 * false if the file is only temporarily cached. 
	 */
	public void setStoredlocally(boolean storedlocally) {
		this.storedlocally = storedlocally;
	}

	/**
	 * @return set to true if the file is to be stored on this node,
	 * false if the file is only temporarily cached.
	 */
	public boolean isStoredlocally() {
		return storedlocally;
	}
	
	public byte[] getDataBuffer() {
		return this.databuffer;
	}

	@Override
	public void setExecutionEnvironment(Peer node, NodeConnection prevNode) {
		this.node = node;
		this.prevNode = prevNode;
		
	}
}
