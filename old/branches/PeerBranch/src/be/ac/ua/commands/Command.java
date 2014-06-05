package be.ac.ua.commands;

import be.ac.ua.node.CommSocket;
import be.ac.ua.node.NodeConnection;
import be.ac.ua.node.Peer;

/**
 * @author Kris
 *
 */
public interface Command {
	public abstract void Execute();
	public String toXml();
	public Command fromXml(String xml);
	public String getCommandType();
	public void setControlSocket(CommSocket controlsocket);
	public void setDataSocket(CommSocket datasocket);
	public void setExecutionEnvironment(Peer node, NodeConnection prevNode);
}
