package main;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

import controller.ClientController;

public class StartUp 
{
	public static void main(String[] args) 
    {
		if (args.length != 2)
		{
			System.out.println("usage: java StartUp host port");
			return;
		}
		
		try 
		{
			ClientController client = new ClientController(args);
			
			client.startConnection();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
    }
}
