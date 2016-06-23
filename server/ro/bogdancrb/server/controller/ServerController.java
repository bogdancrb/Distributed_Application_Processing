package ro.bogdancrb.server.controller;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Vector;

import ro.bogdancrb.server.cmds.Commands;
import ro.bogdancrb.server.model.ClientModel;
import ro.bogdancrb.server.model.FileEvent;

public class ServerController
{
	public final static int PACKETSIZE = 1024;
	public final static int MAX_TIME_FOR_ACTION = 2; // in seconds
	
    private static DatagramSocket socket;
    private static int port;
    private static FileEvent fileEvent;
    private static String clientHostIP;
    private static String response;
    private static String reply;
    private static String sourceFilePath = "D:/Programe/Eclipse/Proiecte/Proiect_Distributed_DADR/files/unprocessed";
    private static HashMap<SocketAddress, ClientModel> clients;

	public ServerController(String args[]) 
	{
		try
		{
			port 	= Integer.parseInt(args[0]);
			socket 	= new DatagramSocket(port);
			
			clients = new HashMap<SocketAddress, ClientModel>();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	public static void beginSocketComunication()
	{
		DatagramPacket packet;
		
		try
		{
			System.out.println("Server is now online on IP " + InetAddress.getLocalHost().getHostAddress() + " and PORT " + port);
			
			while (true)
			{
				packet = recieveDataPacket();   
				
				String data = formatData(packet.getData());
				Commands recievedData = Commands.decodeCmd(data);
				
				showPacketInformation(packet, recievedData);
				
				if (clients.get(packet.getSocketAddress()) == null)
				{
					clients.put(packet.getSocketAddress(), new ClientModel());
				}
				
				switch (recievedData)
				{
					case TryConnection:
						sendDataPacket(packet, Commands.encodeCmd(Commands.Connected));
						break;
						
					case MACAddress:
						packet = recieveDataPacket();
						data = formatData(packet.getData());
						
						clients.get(packet.getSocketAddress()).setMacAddress(data);
						
						System.out.println("Client with MAC ID " + data + " is now connected");
						break;
						
					case SendList:
						File folder = new File(sourceFilePath);
						File[] listOfFiles = folder.listFiles();

						for (int i = 0; i < listOfFiles.length; i++) 
						{
							if (listOfFiles[i].isFile()) 
							{
								sendDataPacket(packet, listOfFiles[i].getName());
							}
						}
						
						sendDataPacket(packet, "exit");
						break;
						
					case ChoseTask:
						packet = recieveDataPacket();
						data = formatData(packet.getData());
						
						fileEvent = getFileEvent(sourceFilePath + "/" + data);
						
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			            ObjectOutputStream os = new ObjectOutputStream(outputStream);
			            
			            os.writeObject(fileEvent);
			            
			            sendDataPacket(packet, Commands.encodeCmd(Commands.SendTask));
			            sendDataPacket(packet, outputStream.toString());
			            
			            fileEvent = getFileEvent(sourceFilePath + "/" + data.replace(".jar", ".bat"));
			            
			            outputStream = new ByteArrayOutputStream();
			            os = new ObjectOutputStream(outputStream);
			            
			            os.writeObject(fileEvent);
			            
			            sendDataPacket(packet, outputStream.toString());
						break;
						
					case SendResult:
						break;
						
					default:
						break;
				}       	   
			}  
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
    
    public static void sendDataPacket(DatagramPacket packet, String data) throws IOException
    {
    	data += "\0";
    	DatagramPacket packetToSend = new DatagramPacket(data.getBytes(), data.getBytes().length, packet.getAddress(), packet.getPort());
    	socket.send(packetToSend);
    }
    
    public static DatagramPacket recieveDataPacket() throws IOException
    {
    	DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
    	socket.receive(packet);
    	
    	return packet;
    }
    
    public static String formatData(byte[] dataByte)
    {
    	String data = new String(dataByte);
    	int escapeCharPosition = data.indexOf("\0");
    	
    	return data.substring(0, escapeCharPosition);
    }
    
    public static void showPacketInformation(DatagramPacket packet, Commands recievedData)
    {
    	System.out.println(packet.getAddress() + " " + packet.getPort() + ": " + Commands.getCmdStringRepresentation(recievedData));
    }

    public static FileEvent getFileEvent(String sourceFilePath) 
    {
        FileEvent fileEvent = new FileEvent();
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
        
        fileEvent.setDestinationDirectory(path);
        fileEvent.setFilename(fileName);
        fileEvent.setSourceDirectory(path);
        
        File file = new File(sourceFilePath);
        
        if (file.isFile()) 
        {
            try 
            {
                DataInputStream diStream = new DataInputStream(new FileInputStream(file));
                long len = (int) file.length();
                byte[] fileBytes = new byte[(int) len];
                int read = 0;
                int numRead = 0;
                
                while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) 
                {
                    read = read + numRead;
                }
                
                diStream.close();
                
                fileEvent.setFileSize(len);
                fileEvent.setFileData(fileBytes);
                fileEvent.setStatus("Success");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
                fileEvent.setStatus("Error");
            }
        } 
        else 
        {
            System.out.println("path specified is not pointing to a file");
            fileEvent.setStatus("Error");
        }
        return fileEvent;
    }
}
