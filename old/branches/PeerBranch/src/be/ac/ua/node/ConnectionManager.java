/**
 * 
 */
package be.ac.ua.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kris
 *
 */
public class ConnectionManager {
	private CommSocket controlsocket;
	private CommSocket datasocket;
	
	public ConnectionManager () {
		this.controlsocket = null;
		this.datasocket = null;
	}
	
	public boolean IsConnected(){
		if (this.controlsocket != null ){
			return this.controlsocket.IsConnected();
		}
		else {
			return false;
		}
		
	}
	
	public void RegisterConnection (String host, int port){
		this.controlsocket = new CommSocket(host, port);
		this.datasocket = new CommSocket(host, port + 1);
	}
	
	public void CloseConnection(){
		if (controlsocket != null){
			controlsocket.Disconnect();
		}
		if (datasocket != null){
			datasocket.Disconnect();
		}
	}
	
	public void SendCommand (String cmd){
		if (controlsocket != null && controlsocket.IsConnected() && (controlsocket.IsClosed() == false)){
			controlsocket.WriteLine(cmd);
		}
		else {
			System.out.println("Socket is not connected to slave");
		}
	}
	
	public String ReceiveResponse (){
		return this.controlsocket.ReadText();
	}
	
	public void ReceiveData (String filename, int datalength){
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(this.datasocket.ReadData(datalength), 0 , datalength);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void SendData (byte[] data){
		this.datasocket.WriteData(data);
	}
	
	public void SendFile (String filename){
		File file = new File (filename);
		byte[] data = new byte[(int) file.length()];
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(data, 0, data.length);
			bis.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not read file " + e.getMessage());
			e.printStackTrace();
		} 
		this.datasocket.WriteData(data);
	}
	
}
