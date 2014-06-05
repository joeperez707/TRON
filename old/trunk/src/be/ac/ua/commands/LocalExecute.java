/**
 * 
 */
package be.ac.ua.commands;

import java.io.IOException;

import be.ac.ua.node.CommSocket;

/**
 * @author Kris
 *
 */
public class LocalExecute implements Command{
	private String oscmd;
	private CommSocket controlsocket;
	
	public LocalExecute(String oscmd){
		this.oscmd = oscmd;
		this.setControlSocket(null);
	}
	
	public void Execute() {
		try {
			System.out.println("Executing command: " + this.oscmd);
			Runtime.getRuntime().exec(this.oscmd);
		} catch (IOException e) {
			System.out.println("Problem executing local command " + this.oscmd + " " + e.getMessage());
			e.printStackTrace();
		}	
		controlsocket.WriteLine("Ready");
	}

	public String getOscmd() {
		return oscmd;
	}

	public void setOscmd(String oscmd) {
		this.oscmd = oscmd;
	}

	public String toXml() {
		return XmlEncoder.XmlEncode(this);
	}

	public Command fromXml(String xml) {
		return XmlDecoder.XmlDecode(xml);
	}

	public String getCommandType() {
		return "LocalExecute";
	}

	public void setControlSocket(CommSocket controlsocket) {
		this.controlsocket = controlsocket;
	}

	public CommSocket getControlSocket() {
		return controlsocket;
	}

	@Override
	public void setDataSocket(CommSocket datasocket) {
		;// No need for a data socket in this command class
	}

}
