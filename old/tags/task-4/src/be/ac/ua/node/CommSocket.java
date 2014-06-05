package be.ac.ua.node;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * 
 */

/**
 * @author Kris
 *
 */
public class CommSocket{
	private String host;
	private int port;
	private Socket socket;
	//private BufferedReader in;
	//private BufferedWriter out;
	
	public CommSocket(String host, int port){
		this.setHost(host);
		this.setPort(port);
		try {
			this.socket = new Socket(host, port);
			socket.setKeepAlive(true);
			this.socket.setSoTimeout(1000);
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not connect to host");
			e.printStackTrace();
		}
	}
	
	public CommSocket(Socket socket){
		this.socket = socket;
		this.host = socket.getInetAddress().getHostName();
		this.port = socket.getPort();
	}

	public boolean IsConnected(){
		return socket.isConnected();
	}
	
	public boolean IsClosed(){
		return socket.isClosed();
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void Connect(String host, int port) {
		//SocketAddress address = new InetSocketAddress(host, port);
		 try {
			socket = new Socket(host, port);
			socket.setKeepAlive(true);
			//commSocket.connect(address);
			//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Could not connect to host");
			e.printStackTrace();
		}	
	}
	
	public void Connect(){
		try {
			socket = new Socket(this.host, this.port);
			socket.setKeepAlive(true);
			//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not connect to host");
			e.printStackTrace();
		}
	}
	
	public void Reconnect(){
		SocketAddress address = new InetSocketAddress(host, port);
		try {
			socket.connect(address);
			//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Could not connect to host");
			e.printStackTrace();
		}
	}

	public String ReadText() {
		String str = null;
		String totalstr = "";
	    try {
	    	BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
//	    	while ( (str = in.readLine()) != null){
	    	str = in.readLine();	
	    	totalstr = totalstr + str;
	//    	}
	    } catch( IOException e ) {
	      e.printStackTrace();
	      try {
			this.socket.close();
		} catch (IOException e1) {
			System.out.println("Socket already closed");
			e1.printStackTrace();
		}
	    }
	    return totalstr;
	}
	
	public byte[] ReadData(int datalength) {
		int bytesread, offset = 0;
		byte [] data = new byte[datalength];
		try{
			DataInputStream dis = new DataInputStream(this.socket.getInputStream());
			bytesread = dis.read(data, 0, datalength);
			offset = bytesread;
			while (dis.available() > 0){
				bytesread = dis.read(data, offset, datalength - offset);
				offset += bytesread;
			}
		} catch (IOException e) {
			System.out.println("Could not read data: " + e.getMessage());
			e.printStackTrace();
		}
		return data;
	}

	public void WriteLine(String msg) {
		try {
			PrintWriter out = new PrintWriter(this.socket.getOutputStream());
			msg += '\n';
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void WriteData(byte[] data) {
		try {
			DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
			dos.write(data, 0, data.length);
			dos.flush();
		} catch (IOException e) {
			System.out.println("Could not write data: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void Disconnect(){
		try {
			//in.close();
			//out.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Could not disconnect");
			e.printStackTrace();
		}
	}
}
