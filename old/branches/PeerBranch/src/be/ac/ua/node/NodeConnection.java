/**
 * 
 */
package be.ac.ua.node;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

import be.ac.ua.commands.Command;
import be.ac.ua.commands.XmlDecoder;

/**
 * A NodeConnection object is responsible for the connection to the next node in the ring network.
 * Two NodeSocket objects are used. The first object is responsible for transmission of control information, the second
 * is responsible for transmitting data over the network.
 * 
 * @author kris
 *
 */
public class NodeConnection {
	private SocketInterface controlsocket;
	private SocketInterface datasocket;
	
	public NodeConnection(SocketInterface controlsocket, SocketInterface datasocket){
		this.controlsocket = controlsocket;
		this.datasocket = datasocket;
	}
	
	public NodeConnection(String host, int port) throws IOException, UnknownHostException{
		this.controlsocket = new NodeSocket(host, port);
		this.datasocket = new NodeSocket(host, port+1);
	}
	
	public void pipeConnection(final NodeConnection prevNode) throws IOException{
		final PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);
		new Thread(new Runnable(){
			public void run() {
				try {
					prevNode.read(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}).start();
		write(out);
	}
	
	public void read(PipedInputStream in) throws IOException {
		int buffer;
		while ((buffer = in.read()) != -1) {
			this.write(buffer);
		}
	}
	
	public int read() throws IOException{
		int input = datasocket.read();
		if (input != -1) {
			return input;
		} else {
			throw new IOException();
		}
	}
	
	public void write(PipedOutputStream out) throws IOException, SocketException{
		int buffer = datasocket.read();
		while (buffer != -1) {
			out.write(buffer);
			buffer = datasocket.read();
		}
	}
	
	public void write(int buffer) throws IOException, SocketException {
		((NodeSocket)this.datasocket).write(buffer);
	}
	
	public void sendCommand(String msg){
		try{
			controlsocket.write(msg.getBytes());
		} catch (SocketException e1) {
			System.out.println("Socket has been closed on other end");
		} catch (IOException e) {
			System.out.println("Error sending command");
		}
	}
	
	public void sendCommand(Command command){
		try{
			controlsocket.write(command.toXml().getBytes());
		} catch (SocketException e1) {
			System.out.println("Socket has been closed on other end");
		} catch (IOException e){
			System.out.println("Error sending command");
		}
	}
	
	public Command receiveCommand() throws IOException{
		byte[] buffer = new byte[255];
		int result = controlsocket.read(buffer);
		//try{
			if (result != -1) {
				return XmlDecoder.XmlDecode(new String(buffer));
			} else {
				throw new IOException();
			}
			/*
		} catch (IOException e){
			System.out.println("Error receiving command");
			return null;
		}
		*/
	}
	
	public void close(){
		if (controlsocket != null){
			try{
				controlsocket.close();
			} catch (IOException e) {
				System.out.println("Error closing controlsocket");
			}
			controlsocket = null;
		}
		if (datasocket != null){
			try{
				datasocket.close();
			} catch (IOException e) {
				System.out.println("Error closing datasocket");
			}
			datasocket = null;
		}
	}
}
