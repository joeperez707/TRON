/**
 * 
 */
package be.ac.ua.node;

/**
 * @author kris
 *
 */
public class LaunchNode {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 0;
		String ip;
		if (args[0].isEmpty()){
			ip = "localhost";
		} else{
			ip = args[0];
		}
		
		try {
			port = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e){
			port = 4000;
		}
		catch (ArrayIndexOutOfBoundsException e){
			port = 4000;
		}
		
		Node node = new Node(ip, port);
		
		node.Start();

	}

}
