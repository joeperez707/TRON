package be.ac.ua.node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NodeSocket implements SocketInterface {
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	public NodeSocket(String host, int port) throws IOException, UnknownHostException{
		this(new Socket(host, port));
	}
	
	public NodeSocket(Socket socket) throws IOException{
		this.socket = socket;
		this.is = socket.getInputStream();
		this.os = socket.getOutputStream();
	}
	
	@Override
	public void write(byte[] buffer) throws IOException, SocketException {
		os.write(buffer);
		os.flush();
	}
	
	public void write(int buffer) throws IOException, SocketException{
		os.write(buffer);
		os.flush();
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return is.read(buffer);
	}
	
	public void read(PipedInputStream in) throws IOException{
		this.write(in.read());
	}
	
	public void write(PipedOutputStream out) throws IOException, SocketException{
		out.write(this.read());
	}

	@Override
	public void close() throws IOException {
		is.close();
		os.close();
		socket.close();
	}
}
