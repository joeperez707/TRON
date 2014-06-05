/**
 * 
 */
package be.ac.ua.node;

import be.ac.ua.commands.Command;
import be.ac.ua.commands.Put;
import be.ac.ua.commands.XmlDecoder;

/**
 * @author kris
 *
 */
public class SlaveThread implements Runnable{
	private Node node;
	private CommSocket controlsocket;
	private CommSocket datasocket;
	
	public SlaveThread (CommSocket controlsocket, CommSocket datasocket, Node node){
		this.controlsocket = controlsocket;
		this.datasocket = datasocket;
		this.node = node;
	}
	
	public void Disconnect(){
		controlsocket.Disconnect();
		datasocket.Disconnect();
	}
	
	
	@Override
	public void run() {
		String xmlcmd;
		Command command;
		String cmdtype;
		xmlcmd = controlsocket.ReadText();
		while (!xmlcmd.equalsIgnoreCase("null") && (!controlsocket.IsClosed())){
			/* ... */
			if (xmlcmd != "" && xmlcmd != null && (!xmlcmd.equalsIgnoreCase("null"))){
				if ((cmdtype = XmlDecoder.XmlDecodeCommandType(xmlcmd)) != null){
					System.out.println("Xml encoded command received: " + xmlcmd);
					System.out.println("Decoding...");
					/* check whether the command is a request to update the connection to the next node, 
					 * if so the request needs to be handled by the master.
					 */
					if (cmdtype.equalsIgnoreCase("connectionupdate")){
						node.sendMessage(xmlcmd);
					}
					/*
					else if (cmdtype.equalsIgnoreCase("connectionupdateresponse")){
						node.setPrevId(XmlDecoder.XmlDecodeConnectionUpdateResponsePrevId(xmlcmd));
						System.out.println("Node's new previous id: " + node.getPrevId());
						System.out.println("Node's new next id: " + node.getNextId());
					}*/
					else {
						if ((command = XmlDecoder.XmlDecode(xmlcmd)) != null){
							command.setControlSocket(controlsocket);
							command.setDataSocket(datasocket);
							if (command instanceof Put){
								if (node.getId() <= ((Put)command).getHash()){
									if ((node.getId() < node.getPrevId()) && (node.getPrevId() <= ((Put)command).getHash())){
										((Put)command).setStoredlocally(true);
										command.Execute();
									} else {
										((Put)command).setStoredlocally(false);
										command.Execute();
										node.AddCommand(command);
									}
								} else {
									if (node.getPrevId() <= ((Put)command).getHash()){
										((Put)command).setStoredlocally(true);
										command.Execute();
									} else {
										if (node.getPrevId() > node.getId()){
											((Put)command).setStoredlocally(true);
											command.Execute();
										} else {
											((Put)command).setStoredlocally(false);
											command.Execute();
											node.AddCommand(command);
										}
									}
								}
								
								/*
								if (node.getId() <= ((Put)command).getHash()){
									((Put)command).setInit(false);
									if (node.getId() > node.getPrevId()){
										((Put)command).setStoredlocally(false);
										command.Execute();
										node.AddCommand(command);
									}
									else {
										((Put)command).setStoredlocally(true);
										command.Execute();
									}
								}
								else {
									if (((Put)command).isInit() == true){
										((Put)command).setStoredlocally(false);
										command.Execute();
										node.AddCommand(command);
									}
									else {
										((Put)command).setStoredlocally(true);
										command.Execute();
									}
								}
								*/
							}
							else {
								command.Execute();
							}
						}
					}
				}
			}
			xmlcmd = controlsocket.ReadText();
		}
		Disconnect();
		
	}

}
