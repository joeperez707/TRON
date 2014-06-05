/**
 * 
 */
package be.ac.ua.client;

import java.io.Console;
import java.util.Date;
import java.util.Scanner;


/**
 * @author Kris
 *
 */
public class CLI implements Runnable{
	private static final String NO_CONSOLE = "Error: Console unavailable";
	private static final String UNKNOWN_COMMAND = "Unknown command [%1$s]%n";
	private static final String TIME_FORMAT = "%1$tH:%1$tM:%1$tS";
    private static final String PROMPT = TIME_FORMAT + " $ ";
	private static final String COMMAND_ERROR = "Command error [%1$s]: [%2$s]%n";
    
    private Console console;
    private Client client;
    
    public CLI(Client client) {
    	this.console = System.console();
    	this.client = client;
    
    	
    }

	@Override
	public void run() {
		if (console != null) {
			while (true)
	    	{
	    		int numberparams = 0;
	    		String commandLine = console.readLine(PROMPT, new Date());
	    		Scanner scanner = new Scanner(commandLine);
	    		
	    		if (scanner.hasNext()){
	    			String command = scanner.next().toUpperCase();
	    			
	    			try {
	    				final CLICommand clicommand = Enum.valueOf(CLICommand.class, command);
	    				String [] params = new String [10];
	    				while (scanner.hasNext() && numberparams < 10){
	    					params[numberparams] = scanner.next();
	    					numberparams++;
	    				}
	    				clicommand.exec(console, params, client, new CLICommand.Listener() {
	    					
	    					@Override
	    					public void exception(Exception e) {
	    						console.printf(COMMAND_ERROR, clicommand, e.getMessage());
	    					}
	    				});
	    			}
	    			catch (IllegalArgumentException e)
	    			{
	    				console.printf(UNKNOWN_COMMAND, command);
	    			}
	    		}
	    	}
    	}
    	else {
    		throw new RuntimeException(NO_CONSOLE);
    	}
	}
}
