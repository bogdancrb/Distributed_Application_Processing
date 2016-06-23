package controller;

import java.io.*;
import java.net.*;

import cmds.Commands;
import ro.bogdancrb.server.model.FileEvent;

public class ClientController 
{
	private final static int PACKETSIZE = 1024;
	
    private DatagramSocket socket;
    private static FileEvent fileEvent;
    private static String filePath;
    private InetAddress serverHostName;
    private int port;
    private String response;
    private String reply;

    public ClientController(String args[]) throws UnknownHostException, URISyntaxException
    {
    	serverHostName 	= InetAddress.getByName(args[0]);
    	port         	= Integer.parseInt(args[1]);
    	filePath		= ClientController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    	socket = null;
    }
    
    public void startConnection()
    {
    	DatagramPacket packet;
    	
    	int readMode;
    	String data, fileDownload = null;
    	
    	Commands recievedData = null;
    	
    	InputStreamReader inputReader = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(inputReader,1);
    	
    	try
        {
        	socket = new DatagramSocket();
        	
        	System.out.println("Enter '0' to exit");
        	System.out.println("Enter '1' to connect");
        
        	do
        	{
	        	readMode = Integer.parseInt(keyboard.readLine());
	        	
	        	recievedData = null;
	        	
	        	switch (readMode)
	        	{
	        		case 1:
			        	sendDataPacket(Commands.encodeCmd(Commands.TryConnection));
			        	packet = recieveDataPacket();
			        	
			        	data = formatData(packet.getData());
						
						recievedData = Commands.decodeCmd(data);
			        	
			        	break;
			        	
	        		case 2:
	        			sendDataPacket(Commands.encodeCmd(Commands.SendList));
			        	
        				do
        				{
        					packet = recieveDataPacket();
        					data = formatData(packet.getData());
        					
        					if (!data.equals("exit"))
        						System.out.println(data);
        				} while (!data.equals("exit"));
        				
        				System.out.println("Enter file name: ");
        				
        				fileDownload = keyboard.readLine();
        				
        				sendDataPacket(Commands.encodeCmd(Commands.ChoseTask));
        				sendDataPacket(fileDownload);
        				
        				packet = recieveDataPacket();
			        	
			        	data = formatData(packet.getData());
						
						recievedData = Commands.decodeCmd(data);
        				
	        			break;
	        		
	        		case 3:
	        			byte[] cmdConsole = ("cmd /c start " + filePath.replace("/","\\").substring(1, filePath.length()) + "\\" + fileDownload.replace(".jar", ".bat")).getBytes();
		                Runtime.getRuntime().exec(new String(cmdConsole));
	        			break;
			        	
	        		default:
	        			return;
	        	}
	        	
	        	if (recievedData != null)
	        	{
		        	switch (recievedData)
					{
						case Connected:
				        	System.out.println("[2] Get list of tasks");
				        	System.out.println("[3] Start task");
				        	System.out.println("[0] Exit");
							
							NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
					        byte[] mac = network.getHardwareAddress();
					        
					        StringBuilder sb = new StringBuilder();
					        
					        for (int i = 0; i < mac.length; i++) 
					        {
					            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
					        }
					        
					        sendDataPacket(Commands.encodeCmd(Commands.MACAddress));
					        sendDataPacket(sb.toString());
							break;
							
						case SendTask:
							packet = recieveDataPacket();
							
							ByteArrayInputStream in = new ByteArrayInputStream(packet.getData());
			                ObjectInputStream is = new ObjectInputStream(in);
			                
			                createAndWriteFile(packet, fileDownload);   // writing the file to hard disk
			                
			                packet = recieveDataPacket();
							
							in = new ByteArrayInputStream(packet.getData());
			                is = new ObjectInputStream(in);
			                
			                createAndWriteFile(packet, fileDownload.replace(".jar", ".bat"));   // writing the file to hard disk
			                
						default:
							break;
					}  
	        	}
        	} while (true);
        }
        catch (Exception e)
        {
           System.out.println(e);
        }
        finally
        {
           if (socket != null)
           {
              socket.close();
           }
        }
    }
    
    private void sendDataPacket(String data) throws IOException
    {
    	data += "\0";
    	DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, serverHostName, port);
    	socket.send(packet);
    }
    
    private DatagramPacket recieveDataPacket() throws IOException
    {
    	DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
    	socket.receive(packet);
    	
    	return packet;
    }
    
    private String formatData(byte[] dataByte)
    {
    	String data = new String(dataByte);
    	int escapeCharPosition = data.indexOf("\0");
    	
    	return data.substring(0, escapeCharPosition);
    }

    public static void createAndWriteFile(DatagramPacket packet, String fileDownload) 
    {
        String outputFile = filePath + fileDownload;
        
        File dstFile = new File(outputFile);
        FileOutputStream fileOutputStream = null;
        
        try 
        {
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(packet.getData());
            fileOutputStream.flush();
            fileOutputStream.close();
             
            System.out.println("Output file : " + outputFile + " is successfully saved ");
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
