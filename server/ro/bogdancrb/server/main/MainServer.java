package ro.bogdancrb.server.main;

import ro.bogdancrb.server.controller.ServerController;

public class MainServer 
{
    public static void main(String args[])
    {
    	if (args.length != 1)
        {
           System.out.println("usage: MainServer port");
           
           return ;
        }
    	
    	ServerController server = new ServerController(args);
    	
    	server.beginSocketComunication();
    }
}
