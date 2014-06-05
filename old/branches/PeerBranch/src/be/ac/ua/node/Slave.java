package be.ac.ua.node;

import java.io.IOException;
import java.net.ServerSocket;

//import be.ac.ua.commands.Command;
//import be.ac.ua.commands.Put;
//import be.ac.ua.commands.XmlDecoder;

public class Slave {
	private ServerSocket controlserversocket;
	private ServerSocket dataserversocket;
	private CommSocket controlsocket;
	private CommSocket datasocket;
	private int ListenerPort;
	private Node node;
	
	public Slave(Node node, int port){
		this.node = node;
		this.ListenerPort = port;
		try {
			controlserversocket = new ServerSocket(ListenerPort);
			dataserversocket = new ServerSocket(ListenerPort + 1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void Start(){
		try{
			while (true){
				System.out.println("Accepting incoming connection requests on controlsocket");
				controlsocket = new CommSocket(controlserversocket.accept());
				System.out.println("Incoming connection accepted on controlsocket from: " + controlsocket.getHost());
				System.out.println("Accepting incoming connection requests on datasocket");
				datasocket = new CommSocket(dataserversocket.accept());
				System.out.println("Incoming connection accepted on datasocket from: " + controlsocket.getHost());
				new Thread(new SlaveThread(controlsocket, datasocket, this.node)).start();
			}
		} catch (IOException e) {
			System.out.println("Accepting incoming connections failed " + e.getMessage());
			e.printStackTrace();
		}
	}
	
//	public void Disconnect(){
//		controlsocket.Disconnect();
//		datasocket.Disconnect();
//	}
//
//	@Override
//	public void run() {
//		String xmlcmd;
//		Command command;
//		String cmdtype;
//		try {
//			System.out.println("Accepting incoming connection requests on controlsocket");
//			controlsocket = new CommSocket(controlserversocket.accept());
//			System.out.println("Incoming connection accepted on controlsocket from: " + controlsocket.getHost());
//			System.out.println("Accepting incoming connection requests on datasocket");
//			datasocket = new CommSocket(dataserversocket.accept());
//			System.out.println("Incoming connection accepted on datasocket from: " + controlsocket.getHost());
//			
//			while (controlsocket.IsClosed() == false){
//				xmlcmd = controlsocket.ReadText();
//				/* ... */
//				if (xmlcmd != "" && xmlcmd != null && (!xmlcmd.equalsIgnoreCase("null"))){
//					System.out.println("Xml encoded command received: " + xmlcmd);
//					System.out.println("Decoding...");
//					if ((cmdtype = XmlDecoder.XmlDecodeCommandType(xmlcmd)) != null){
//						/* check whether the command is a request to update the connection to the next node, 
//						 * if so the request needs to be handled by the master.
//						 */
//						if (cmdtype.equalsIgnoreCase("connectionupdate")){
//							node.sendMessage(xmlcmd);
//						}
//						else {
//							if ((command = XmlDecoder.XmlDecode(xmlcmd)) != null){
//								command.setControlSocket(controlsocket);
//								command.setDataSocket(datasocket);
//								if (command instanceof Put){
//									if (node.getId() <= ((Put)command).getHash()){
//										((Put)command).setInit(false);
//										if (node.getId() > node.getPrevId()){
//											((Put)command).setStoredlocally(false);
//											command.Execute();
//											node.AddCommand(command);
//										}
//										else {
//											((Put)command).setStoredlocally(true);
//											command.Execute();
//										}
//									}
//									else {
//										if (((Put)command).isInit() == true){
//											((Put)command).setStoredlocally(false);
//											command.Execute();
//											node.AddCommand(command);
//										}
//										else {
//											((Put)command).setStoredlocally(true);
//											command.Execute();
//										}
//									}
//								}
//								else {
//									command.Execute();
//								}
//							}
//						}
//					}
//				}
//			}
//			Disconnect();
//			this.run();
//		} catch (IOException e) {
//			System.out.println("Accepting incoming connections failed " + e.getMessage());
//			e.printStackTrace();
//		}
//	}
}
