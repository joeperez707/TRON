/**
 * 
 */
package be.ac.ua.node;

import java.io.IOException;

/**
 * @author kris
 *
 */
public interface SocketInterface {
	public void write (byte[] b) throws IOException;
	public int read() throws IOException;
	public int read(byte[] b) throws IOException;
	public void close() throws IOException;
}
