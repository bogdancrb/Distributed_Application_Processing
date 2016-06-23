package ro.bogdancrb.server.thread;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Vector;

import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;
import com.sun.security.ntlm.Client;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ServerContext;

import ro.bogdancrb.server.cmds.Commands;
import ro.bogdancrb.server.controller.ServerController;
import ro.bogdancrb.server.model.ClientModel;
import ro.bogdancrb.server.states.ClientStates;

public class ProcessThread extends Thread
{
	private int threadType;
	private Vector<ClientModel> clients;
	private HashMap<SocketAddress, ClientModel> clientsUpdate;
	
	public ProcessThread(int threadType)
	{
		clients = new Vector<ClientModel>();
		clientsUpdate = new HashMap<SocketAddress, ClientModel>();
		this.threadType = threadType;
	}
	
	public void addClient(DatagramPacket packet, String macAddress) 
	{		
		clients.add(new ClientModel());
		
		clients.lastElement().setIpAddress(packet.getAddress());
		clients.lastElement().setPort(packet.getPort());
		clients.lastElement().setMacAddress(macAddress);
		clients.lastElement().setPacket(packet);
		clients.lastElement().setState(ClientStates.Idle);
	}
	
	private void busy()
	{
		try 
		{
			sleep(ServerController.MAX_TIME_FOR_ACTION * 1000);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		if (!clients.isEmpty())
		{
			switch (threadType)
			{
				case 1: // "Active"
					
					break;
				case 2: // "Idle"
					try
					{
						while (true)
						{
							clients.firstElement().setPacket(ServerController.recieveDataPacket());   
							
							DatagramPacket packet = clients.firstElement().getPacket();
							
							String data = ServerController.formatData(packet.getData());
							Commands recievedData = Commands.decodeCmd(data);
							
							if (clientsUpdate.get(packet.getSocketAddress()) == null)
							{
								clientsUpdate.put(packet.getSocketAddress(), new ClientModel());
							}
							else
							{
								Object update = clientsUpdate.get(packet.getSocketAddress());
								
								if (clients.firstElement().getMacAddress() == ((ClientModel) update).getMacAddress())
								{
									if (((ClientModel) update).getDestinationPath() != null)
										clients.firstElement().setDestinationPath(((ClientModel) update).getDestinationPath());
									
									if (((ClientModel) update).getState() != null)
										clients.firstElement().setState(((ClientModel) update).getState());
								}
							}
							
							switch (recievedData)
							{
								case FilePath:
									packet = ServerController.recieveDataPacket();
									data = ServerController.formatData(packet.getData());
									
									clientsUpdate.get(packet.getSocketAddress()).setSourceFilePath(data);
									break;
									
								case SendList:
									break;
									
								case ChoseTask:
									break;
									
								case SendTask:
									break;
									
								case SendResult:
									break;
									
								default:
									System.out.println("Error " + packet.getAddress() + " " + packet.getPort() + " could not connect to server."); 
									break;
							}   
							
							busy();
							
							clients.addElement(clients.firstElement());
							clients.removeElementAt(0);
						}  
					}
					catch (Exception e)
					{
						System.out.println(e);
					}
					break;
			}
		}
	}
}
