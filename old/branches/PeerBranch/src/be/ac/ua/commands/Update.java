/**
 * 
 */
package be.ac.ua.commands;

import be.ac.ua.node.CommSocket;
import be.ac.ua.node.Multicast;
import be.ac.ua.node.NodeConnection;
import be.ac.ua.node.Peer;

/**
 * @author kris
 *
 */
public class Update implements Command{
	private String host;
	private Multicast multicast;
	private Peer node;
	private NodeConnection nextNode;
	private int previd;
	private int id;
	private int nextid;
	
	public Update(Peer node, NodeConnection prevNode){
		this.node = node;
		this.multicast = node.getMulticast();
		this.nextNode = node.getNextNode();
	}
	
	public Update(String host, int previd, int id, int nextid){
		this.host = host;
		this.previd = previd;
		this.id = id;
		this.nextid = nextid;
	}
	
	public void setExecutionEnvironment(Peer node, NodeConnection prevNode){
		this.node = node;
		this.multicast = node.getMulticast();
		this.nextNode = node.getNextNode();
	}
	
	@Override
	public void Execute() {
		if (multicast.getId() == nextid){
			multicast.setPrevId(id);
		}
		if (id == multicast.getId()){
			System.out.println("Reconnecting to node on address: " + host);
			node.Join(host, 4000);
			multicast.setNextId(nextid);
		}
		else {
			nextNode.sendCommand(this);
			System.out.println("Forwarding connection update to node: " + id);
		}
		System.out.println("Node's prev id: " + multicast.getPrevId());
		System.out.println("Node's next id: " + multicast.getNextId());
	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>connectionupdate</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<id>" + Integer.toString(id) + "</id>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<nextid>" + Integer.toString(nextid) + "</nextid>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<neighbour>" + Integer.toString(previd) + "</neighbour>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<address>" + host + "</address>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append('\n');
		String message = sb.toString();
		return message;
	}

	@Override
	public Command fromXml(String xml) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {
		return "Update";
	}

	@Override
	public void setControlSocket(CommSocket controlsocket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataSocket(CommSocket datasocket) {
		// TODO Auto-generated method stub
		
	}

}
