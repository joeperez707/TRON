package be.ac.ua.commands;

import be.ac.ua.node.CommSocket;

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
}
