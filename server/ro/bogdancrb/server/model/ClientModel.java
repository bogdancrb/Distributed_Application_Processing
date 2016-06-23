package ro.bogdancrb.server.model;

import java.net.DatagramPacket;
import java.net.InetAddress;

import ro.bogdancrb.server.states.ClientStates;

public class ClientModel 
{
	private InetAddress ipAddress;
	private int port;
	private String macAddress;
	private String sourceFilePath;
	private String destinationPath;
	private DatagramPacket packet;
	private ClientStates state;
	
	public InetAddress getIpAddress()
	{
		return this.ipAddress;
	}
	
	public int getPort() 
	{
		return this.port;
	}
	
	public String getMacAddress()
	{
		return this.macAddress;
	}
	
	public String getSourceFilePath()
	{
		return this.sourceFilePath;
	}
	
	public String getDestinationPath()
	{
		return this.destinationPath;
	}
	
	public DatagramPacket getPacket() 
	{
		return this.packet;
	}
	
	public ClientStates getState() 
	{
		return this.state;
	}
	
	public void setIpAddress(InetAddress ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
	public void setMacAddress(String macAddress)
	{
		this.macAddress = macAddress;
	}
	
	public void setSourceFilePath(String sourceFilePath)
	{
		this.sourceFilePath = sourceFilePath;
	}
	
	public void setDestinationPath(String destinationPath)
	{
		this.destinationPath = destinationPath;
	}

	public void setPort(int port) 
	{
		this.port = port;
	}

	public void setPacket(DatagramPacket packet) 
	{
		this.packet = packet;
	}

	public void setState(ClientStates state) 
	{
		this.state = state;
	}
}