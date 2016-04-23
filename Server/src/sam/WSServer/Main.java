package sam.WSServer;

import sam.WSServer.Servers.SSLServer;
import sam.WSServer.Servers.Server;

public class Main
{
	public static void main(String[] args)
	{
		Utils.logMessage("Starting Server");
		Server server = new Server(9000);
		server.start();
		SSLServer sslServer = new SSLServer(9001);
		sslServer.start();
	}
}
