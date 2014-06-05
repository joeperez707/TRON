/**
 * 
 */
package be.ac.ua.node;

/**
 * The NodeInfo object contains information about the node,
 * such as it's address, listener port, id and it's neighbour's id's.
 * 
 * @author kris
 *
 */
public class NodeInfo {
	private int id;
	private int nextid;
	private int previd;
	private String host;
	private int port;
	
	public NodeInfo(String host, int port){
		this.setHost(host);
		this.setPort(port);
		this.setId(0);
		this.setNextid(0);
		this.setPrevid(0);
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param nextid the nextid to set
	 */
	public void setNextid(int nextid) {
		this.nextid = nextid;
	}

	/**
	 * @return the nextid
	 */
	public int getNextid() {
		return nextid;
	}

	/**
	 * @param previd the previd to set
	 */
	public void setPrevid(int previd) {
		this.previd = previd;
	}

	/**
	 * @return the previd
	 */
	public int getPrevid() {
		return previd;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	
}
