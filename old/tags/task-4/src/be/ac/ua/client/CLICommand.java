/**
 * 
 */
package be.ac.ua.client;

import java.io.Console;

import be.ac.ua.commands.Get;
import be.ac.ua.commands.LocalExecute;
import be.ac.ua.commands.Put;

/**
 * @author kris
 *
 */
public enum CLICommand {
	GET (new GetAction()), 
	PUT (new PutAction()),
	LOCALEXECUTE (new LocalExecuteAction()),
	JOIN (new JoinAction()),
	LEAVE (new LeaveAction()),
	EXIT (new ExitAction()),
	LIST (new ListAction()),
	FIND ( new FindAction());
	
	
	public interface Listener
	{
		public void exception(Exception e);
	}
	
	private interface Action
	{
		public void exec(Console console, String[] params, Client client) throws Exception;
	}
	
	private static class GetAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			if (params[0] != null) {
				client.AddCommand(new Get(params[0]));
				console.printf("File %1$s is scheduled for transfer", params[0]);
			}
			else {
				throw new Exception("Not enough parameters: filename is missing");
			}
		}
	}
	
	private static class PutAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			if (params[0] != null){
				Put putcmd = new Put(params[0]);
				client.AddCommand(putcmd);
				console.printf("Initiating put command with hash: " + Integer.toString(putcmd.getHash()) + "\n");
			}
			else {
				throw new Exception("Not enough parameters: filename is missing");
			}
		}	
	}
	
	private static class LocalExecuteAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			if (params[0] != null) {
				client.AddCommand(new LocalExecute(params[0]));
			}
			else {
				throw new Exception("Not enough parameters: execution command is missing");
			}
		}
	}
	
	private static class JoinAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			if (params[0] != null){
				console.printf("Node is connecting to [%1$s:%2$s]%n", params[0], 4002);
				client.Leave();
				client.Join(params[0], 4002);
			}
			else {
				throw new Exception("Not enough parameters: host is missing");
			}
		}
	}
	
	private static class ListAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			client.ListNodes();
		}
		
	}
	
	private static class FindAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			client.FindNodes();
		}
		
	}
	
	private static class LeaveAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			console.printf("Node has been disconnected");
			client.Leave();
		}	
	}
	
	private static class ExitAction implements Action {

		@Override
		public void exec(Console console, String[] params, Client client) throws Exception {
			System.exit(0);
		}
	}
	
	private Action action;
	
	private CLICommand (Action action)
	{
		this.action = action;
	}
	
	public void exec(Console console, String[] params, Client client, final Listener listener)
	{
		try {
			action.exec(console, params, client);
		}
		catch (Exception e)
		{
			listener.exception(e);
		}
	}
}
