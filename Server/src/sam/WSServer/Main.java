package sam.WSServer;

import sam.WSServer.Servers.Server;

public class Main
{
	public static void main(String[] args)
	{
		Utils.logMessage("Starting Server");
		Server server = new Server(9000);
		server.start();
	}
}
