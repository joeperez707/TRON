package be.ac.ua.node;

import java.util.concurrent.LinkedBlockingQueue;

import be.ac.ua.commands.Command;
import be.ac.ua.commands.Get;
import be.ac.ua.commands.LocalExecute;
import be.ac.ua.commands.Put;
import be.ac.ua.commands.XmlDecoder;


public class Master implements Runnable
{
	private LinkedBlockingQueue<Command> elements;
	private ConnectionManager connectionmanager;
	private Node node;
	
	public Master(Node node){	
		this.node = node;
		this.elements = new LinkedBlockingQueue<Command>();
		this.connectionmanager = new ConnectionManager();
	}
 
	public void AddCommand(Command command){
		try {
			elements.put(command);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
 
	public void Execute(Command command){
		while (!connectionmanager.IsConnected());
		String xmlcmd = command.toXml();
		connectionmanager.SendCommand (xmlcmd);
		if (command instanceof Put) {
			connectionmanager.SendData(((Put)command).getDataBuffer());
			String response = connectionmanager.ReceiveResponse();
			if (response.equalsIgnoreCase("READY")){
				System.out.println("File sent succesfully");
			}
		} else if (command instanceof Get) {
			String response = connectionmanager.ReceiveResponse();
			int filesize = Integer.parseInt(response);
			String filename = ((Get)command).getId();
			if (filesize > 0) {
				connectionmanager.ReceiveData(filename, filesize);
			}
			else {
				System.out.println("Error while receiving file: " + filename);
			}		
		} else if (command instanceof LocalExecute){
			String response = connectionmanager.ReceiveResponse();
			if (response.equalsIgnoreCase("READY")){
				System.out.println("Command: " + ((LocalExecute)command).getOscmd() + " has been executed");
			}
			else {
				System.out.println("Error while executing command: " + ((LocalExecute)command).getOscmd());
			}
		} else {
			System.out.println("Cannot handle unknown command object");
		}
	}
	
	public void Join (String host, int port){
		Leave();
		connectionmanager.RegisterConnection(host, port);
	}
	
	public void Leave (){
		connectionmanager.CloseConnection();
	}
	
	public void ConnectionCheck (){
		String connectionupdate;
		if ((connectionupdate = node.receiveMessage()) != null) {
			int neighbourid = XmlDecoder.XmlDecodeConnectionUpdateNeighbourId(connectionupdate);
			if (neighbourid == node.getId()){
				node.setPrevId(XmlDecoder.XmlDecodeConnectionUpdatePrevId(connectionupdate));
			}
			int id = XmlDecoder.XmlDecodeConnectionUpdateId(connectionupdate);
			if (id == node.getId()){
				String address = XmlDecoder.XmlDecodeConnectionUpdateAddress(connectionupdate);
				System.out.println("Reconnecting to node on address: " + address);
				Join (address, 4000);
				node.setNextId(XmlDecoder.XmlDecodeConnectionUpdateNextId(connectionupdate));
				//connectionmanager.SendCommand(XmlEncoder.XmlEncodeConnectionUpdateResponse(node.getNextId(), node.getId()));
			}
			else {
				connectionmanager.SendCommand(connectionupdate);
				System.out.println("Forwarding connection update to node: " + id);
			}
			System.out.println("Node's prev id: " + node.getPrevId());
			System.out.println("Node's next id: " + node.getNextId());
		}
	}
	
	@Override
	public void run() {
		while (true) {
			ConnectionCheck();
			if (! elements.isEmpty()){
				try {
					this.Execute(elements.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
