package be.ac.ua.commands;


public class XmlDecoder {

	public static Command XmlDecode (String xmlcmd){
		String cmdtype = XmlDecodeCommandType(xmlcmd);
        if (cmdtype.equalsIgnoreCase("Put")){
        	String id = getTagText(xmlcmd, "filename");
        	String filesize = getTagText(xmlcmd, "filesize");
        	Put cmd = new Put(id, Integer.parseInt(filesize));
        	cmd.setHash(Integer.parseInt(getTagText(xmlcmd, "hash")));
        	return cmd;
        } else if (cmdtype.equalsIgnoreCase("Get")){
        	String id = getTagText(xmlcmd, "filename");
        	Command cmd = new Get(id);
        	return cmd;
        } else if (cmdtype.equalsIgnoreCase("LocalExecute")){
        	String oscmd = getTagText(xmlcmd, "value");
        	Command cmd = new LocalExecute(oscmd);
        	return cmd;
        } else if (cmdtype.equalsIgnoreCase("Shutdown")) {
        	String id = getTagText(xmlcmd, "id");
        	Command cmd = new Shutdown(Integer.parseInt(id));
        	return cmd;
        } else {
        	return null;
        }
    }

    public static String getTagText(String xmlContent, String tag) {
        String content = null;
        int start = (xmlContent.indexOf("<" + tag + ">")) + (("<" + tag + ">").length());
        int end = xmlContent.indexOf("</" + tag + ">");

        if (start > -1 && end > -1) {
            content = xmlContent.substring(start, end);
        }

        return content;
    }
    
    public static String XmlDecodeCommandType(String xmlcmd){
    	return getTagText(xmlcmd, "type");
    }
    
    public static int XmlDecodeMulticastMessageId(String message){
    	return Integer.parseInt(getTagText(message, "id"));
    }
    
    public static int XmlDecodeMulticastMessagePort(String message){
    	return Integer.parseInt(getTagText(message, "port"));
    }
    
    public static String XmlDecodeMulticastMessageAddress(String message){
    	return getTagText(message, "address");
    }
    
    public static int XmlDecodeConnectionUpdateId(String message){
    	return Integer.parseInt(getTagText(message, "id"));
    }
    
    public static int XmlDecodeConnectionUpdateResponseId(String message){
    	return Integer.parseInt(getTagText(message, "id"));
    }
    
    public static int XmlDecodeConnectionUpdateNextId(String message){
    	return Integer.parseInt(getTagText(message, "nextid"));
    }
    
    public static int XmlDecodeConnectionUpdateNeighbourId(String message){
    	return Integer.parseInt(getTagText(message, "neighbour"));
    }
    
    public static int XmlDecodeConnectionUpdatePrevId(String message){
    	return Integer.parseInt(getTagText(message, "nextid"));
    }
    
    public static int XmlDecodeConnectionUpdateResponsePrevId(String message){
    	return Integer.parseInt(getTagText(message, "previd"));
    }
    
    public static String XmlDecodeConnectionUpdateAddress(String message){
    	return getTagText(message, "address");
    }
}
