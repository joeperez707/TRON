/**
 * 
 */
package be.ac.ua.node;

import java.io.IOException;
import java.net.ServerSocket;

import be.ac.ua.commands.Command;
import be.ac.ua.commands.Put;
import be.ac.ua.commands.Get;
import be.ac.ua.commands.XmlDecoder;

/**
 * @author kris
 *
 */
public class ClientHandler implements Runnable{
	private ServerSocket controlserversocket;
	private ServerSocket dataserversocket;
	private CommSocket controlsocket;
	private CommSocket datasocket;
	private Node node;
	private int ListenerPort;
	
	public ClientHandler (Node node, int port) {
		this.node = node;
		this.ListenerPort = port;
		try {
			controlserversocket = new ServerSocket(ListenerPort + 2);
			dataserversocket = new ServerSocket(ListenerPort + 3);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void Disconnect(){
		controlsocket.Disconnect();
		datasocket.Disconnect();
	}

	@Override
	public void run() {
		String xmlcmd;
		Command command;
		try {
			System.out.println("Accepting incoming client requests on controlsocket");
			controlsocket = new CommSocket(controlserversocket.accept());
			System.out.println("Incoming connection accepted on controlsocket from: " + controlsocket.getHost());
			System.out.println("Accepting incoming client requests on datasocket");
			datasocket = new CommSocket(dataserversocket.accept());
			System.out.println("Incoming connection accepted on datasocket from: " + datasocket.getHost());
			
			xmlcmd = controlsocket.ReadText();
			while (!xmlcmd.equalsIgnoreCase("null") && (!(controlsocket.IsClosed()))){
				
			//while (controlsocket.IsClosed() == false){
				//xmlcmd = controlsocket.ReadText();
				/* ... */
				if (xmlcmd != "" && xmlcmd != null && (!xmlcmd.equalsIgnoreCase("null"))){
					if (XmlDecoder.XmlDecodeCommandType(xmlcmd) != null){
						System.out.println("Xml encoded command received from client: " + xmlcmd);
						System.out.println("Decoding...");
						if ((command = XmlDecoder.XmlDecode(xmlcmd)) != null){
							command.setControlSocket(controlsocket);
							command.setDataSocket(datasocket);
							if (command instanceof Put){
								/*
								 * the Put command and the file data should be forwarded
								 * to the next node if the hashcode of the filename is smaller than
								 * the id of the node AND if the id of the node is greater than the id of
								 * the previous node.
								 */
								if ((node.getNextId() == 0) && (node.getPrevId() == 0)){
									((Put)command).setStoredlocally(true);
									command.Execute();
								}
								else {
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
							else if (command instanceof Get) {
								/*
								 * to implement
								 */
							}
							else {
								System.out.println("Other commands cannot be initiated by the client");
							}
						}
					}
				}
				xmlcmd = controlsocket.ReadText();
			}
			Disconnect();
			this.run();
		} catch (IOException e) {
			System.out.println("Accepting incoming connections failed " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	
}
