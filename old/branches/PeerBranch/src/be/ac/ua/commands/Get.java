package be.ac.ua.commands;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import be.ac.ua.node.CommSocket;

/**
 * @author uauser
 *
 */

public class Get implements Command {
	private String id;
	private CommSocket controlsocket;
	private CommSocket datasocket;
	
	private static Logger log = Logger.getLogger(Get.class.getName());
	private final static Logger LOG = Logger.getLogger(Get.class);
	
	public Get(String id){
		this.setId(id);
		this.setControlSocket(null);
		this.setDataSocket(null);
	}
	
	public void Execute(){
		File file = new File(this.id);
		if (file.exists()){
			int filesize = (int) file.length();
			byte[] data = new byte[filesize];
			this.controlsocket.WriteLine(Integer.toString(filesize));
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
			log.info("Executing get command");
		}
		else {
			this.controlsocket.WriteLine("0");
		}
	}

	public String toXml() {
		return XmlEncoder.XmlEncode(this);
	}

	public Command fromXml(String xml) {
		return XmlDecoder.XmlDecode(xml);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getCommandType() {
		return "Get";
	}

	public void setControlSocket(CommSocket controlsocket) {
		this.controlsocket = controlsocket;
	}

	public CommSocket getControlSocket() {
		return controlsocket;
	}

	public void setDataSocket(CommSocket datasocket) {
		this.datasocket = datasocket;
	}

	public CommSocket getDataSocket() {
		return datasocket;
	}
}
