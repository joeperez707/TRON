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
public class Shutdown implements Command {
	private Peer node;
	private Multicast multicast;
	private NodeConnection nextNode;
	private int id;
	
	public Shutdown(int id){
		this.setId(id);
	}
	
	@Override
	public void Execute() {
		if (id == multicast.getNextId()){
			node.Leave();
		}
		else {
			nextNode.sendCommand(this);
		}
		if (id == multicast.getId()){
			System.out.println("This node is shutting down");
			node.setShutdown(true);
			nextNode.sendCommand(new Update(multicast.getNextAddress(),multicast.getPrevId(), multicast.getId(), multicast.getNextId()));
		}
	}
	
	public void setExecutionEnvironment(Peer node, NodeConnection prevNode){
		this.node = node;
		this.multicast = node.getMulticast();
		this.nextNode = node.getNextNode();
	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>" + this.getCommandType() + "</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<id>" + this.getId() + "</id>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append("\n");
		String xmlcmd = sb.toString();
		return xmlcmd;
	}

	@Override
	public Command fromXml(String xml) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {
		return "Shutdown";
	}

	@Override
	public void setControlSocket(CommSocket controlsocket) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataSocket(CommSocket datasocket) {
		// TODO Auto-generated method stub

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
