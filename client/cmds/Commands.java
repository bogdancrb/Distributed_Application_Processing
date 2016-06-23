package cmds;

public enum Commands 
{
	TryConnection,
    Connected,
    MACAddress,
    FilePath,
    SendList,
    ChoseTask,
    SendTask,
    SendResult,
    Error;
	
    public static Commands decodeCmd(String cmd) 
    {
	    switch (Integer.parseInt(cmd)) 
	    {
		    case 1:
		    	return TryConnection;
		    	
	    	case 2:
	    		return Connected;
	    		
	    	case 3:
	    		return MACAddress;
	    		
	    	case 4:
	    		return FilePath;
	    		
	    	case 5:
	    		return SendList;
	    		
	    	case 6:
	    		return ChoseTask;
	    		
	    	case 7:
	    		return SendTask;
	    		
	    	case 8:
	    		return SendResult;
	    		
    		default:
    			return Error;
	    }
    }
    
    public static String encodeCmd(Commands cmd) 
    {
	    switch (cmd) 
	    {
		    case TryConnection:
		    	return "1";
		    	
	    	case Connected:
	    		return "2";
	    		
	    	case MACAddress:
	    		return "3";
	    		
	    	case FilePath:
	    		return "4";
	    		
	    	case SendList:
	    		return "5";
	    		
	    	case ChoseTask:
	    		return "6";
	    		
	    	case SendTask:
	    		return "7";
	    		
	    	case SendResult:
	    		return "8";
	    		
    		default:
    			return "0";
	    }
    }
    
    public static String getCmdStringRepresentation(Commands cmd)
    {
    	switch (cmd) 
	    {
		    case TryConnection:
		    	return "Trying to connect to server";
		    	
	    	case Connected:
	    		return "Connected to server";
	    		
	    	case MACAddress:
	    		return "Sending MAC Address";
	    		
	    	case FilePath:
	    		return "Sending file path info";
	    		
	    	case SendList:
	    		return "Requests list of available tasks";
	    		
	    	case ChoseTask:
	    		return "Requests task from server";
	    		
	    	case SendTask:
	    		return "Recieveing task from server";
	    		
	    	case SendResult:
	    		return "Sending final result to server";
	    		
    		default:
    			return "Connection error";
	    }
    }
}