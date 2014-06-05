package be.ac.ua.commands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import be.ac.ua.node.CommSocket;


/**
 * 
 */

/**
 * @author uauser
 *
 */
public class Put implements Command{
	private String filename;
	private int hash;
	private int datalength;
	private byte[] databuffer;
	private CommSocket datasocket;
	private CommSocket controlsocket;
	private boolean storedlocally;
	private boolean init;
	
	public Put(String filename, int datalength){
		this.setFilename(filename);
		this.setDataSocket(null);
		this.setControlSocket(null);
		this.setDatalength(datalength);
		this.databuffer = new byte[this.datalength];
		this.setHash(Math.abs(filename.hashCode()));
		this.setStoredlocally(false);
		this.setInit(true);
	}
	
	public Put(String filename) {
		this.setFilename(filename);
		this.setDataSocket(null);
		this.setControlSocket(null);
		this.setHash(Math.abs(filename.hashCode()));
		this.setStoredlocally(false);
		File file = new File(filename);
		this.setDatalength((int)file.length());
		this.databuffer = new byte[this.datalength];
		this.setInit(true);
	}
	
	public void Execute (){
		try {
			System.out.println("Receiving file: " + this.filename);
			this.databuffer = this.datasocket.ReadData(datalength);
			System.out.println("Done receiving file");
			if (isStoredlocally()){
				System.out.println("Storing file locally");
				FileOutputStream fos = new FileOutputStream(this.filename);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(this.databuffer, 0 , this.datalength);
				bos.flush();
				bos.close();
			}	
			this.controlsocket.WriteLine("Ready\n");
		} catch (FileNotFoundException e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public String toXml() {
		return XmlEncoder.XmlEncode(this);
	}

	public Command fromXml(String xml) {
		return XmlDecoder.XmlDecode(xml);
	}
	
	public void setHash(int hash) {
		this.hash = hash;
	}
	
	public int getHash() {
		return this.hash;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public String getCommandType() {
		return "Put";
	}

	public void setDataSocket(CommSocket datasocket) {
		this.datasocket = datasocket;
	}

	public CommSocket getDataSocket() {
		return datasocket;
	}

	public void setControlSocket(CommSocket controlsocket) {
		this.controlsocket = controlsocket;
	}

	public CommSocket getControlSocket() {
		return controlsocket;
	}

	public void setDatalength(int datalength) {
		this.datalength = datalength;
	}

	public int getDatalength() {
		return datalength;
	}

	/**
	 * @param set to true if the file is to be stored on this node,
	 * false if the file is only temporarily cached. 
	 */
	public void setStoredlocally(boolean storedlocally) {
		this.storedlocally = storedlocally;
	}

	/**
	 * @return set to true if the file is to be stored on this node,
	 * false if the file is only temporarily cached.
	 */
	public boolean isStoredlocally() {
		return storedlocally;
	}
	
	public byte[] getDataBuffer() {
		return this.databuffer;
	}

	/**
	 * @param set to true if the command has to be send around the ring, set
	 * to false if the file can be stored on the next appropriate node.
	 */
	public void setInit(boolean init) {
		this.init = init;
	}

	public boolean isInit() {
		return init;
	}
}
