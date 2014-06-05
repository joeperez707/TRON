package be.ac.ua.commands;


public class XmlEncoder {
	
	public static String XmlEncode (Command command){
		String cmdtype = command.getCommandType();
		if (cmdtype.equalsIgnoreCase("Get")){
			return XmlEncodeGetCommand((Get) command);
		} else if (cmdtype.equalsIgnoreCase("Put")){
			return XmlEncodePutCommand((Put) command);
		} else if (cmdtype.equalsIgnoreCase("LocalExecute")){
			return XmlEncodeLocalExecuteCommand((LocalExecute) command);
		} else{
			return null;
		}
	}
	
	public static String XmlEncodeGetCommand (Get command){
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>" + command.getCommandType() + "</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<filename>" + command.getId() + "</filename>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append('\n');
		String xmlcmd = sb.toString();
		return xmlcmd;
	}
	
	public static String XmlEncodePutCommand (Put command){
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>" + command.getCommandType() + "</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<filename>" + command.getFilename() + "</filename>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<filesize>" + command.getDatalength() + "</filesize>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<hash>" + command.getHash() + "</hash>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<init>" + command.isInit() + "</init>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append("\n");
		String xmlcmd = sb.toString();
		return xmlcmd;
	}
	
	public static String XmlEncodeLocalExecuteCommand (LocalExecute command){
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>" + command.getCommandType() + "</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<name>oscmd</name>");
		sb.append("<value>" + command.getOscmd() + "</value>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append('\n');
		String xmlcmd = sb.toString();
		return xmlcmd;
	}
	
	public static String XmlEncodeMulticastMessage (String ip, int port, int id){
		StringBuilder sb = new StringBuilder();
		sb.append("<multicast>");
		sb.append("<address>" + ip + "</address>");
		sb.append("<port>" + Integer.toString(port) + "</port>");
		sb.append("<id>" + Integer.toString(id) + "</id>");
		sb.append("</multicast>");
		sb.append('\n');
		String message = sb.toString();
		return message;
	}
	
	public static String XmlEncodeConnectionUpdate (String ip, int id, int nextid, int neighbourid) {
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>connectionupdate</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<id>" + Integer.toString(id) + "</id>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<nextid>" + Integer.toString(nextid) + "</nextid>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<neighbour>" + Integer.toString(neighbourid) + "</neighbour>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<address>" + ip + "</address>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append('\n');
		String message = sb.toString();
		return message;
	}
	
	public static String XmlEncodeConnectionUpdateResponse (int id, int previd) {
		StringBuilder sb = new StringBuilder();
		sb.append("<command>");
		sb.append("<type>connectionupdateresponse</type>");
		sb.append("<properties>");
		sb.append("<property>");
		sb.append("<id>" + Integer.toString(id) + "</id>");
		sb.append("</property>");
		sb.append("<property>");
		sb.append("<previd>" + Integer.toString(previd) + "</previd>");
		sb.append("</property>");
		sb.append("</properties>");
		sb.append("</command>");
		sb.append('\n');
		String message = sb.toString();
		return message;
	}
}
