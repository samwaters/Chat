package sam.WSServer.Servers;

import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

public class SSLServer extends Server
{
	public SSLServer(int port)
	{
		super(port);
	}
	
	protected void createServerSocket()
	{
		try
		{
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			KeyManagerFactory km = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("/Users/sam/Projects/Chat/Server/keystore.jks"), "pass123456".toCharArray());
			km.init(ks, "pass123456".toCharArray());
			sc.init(km.getKeyManagers(), null, null);
			SSLServerSocket socket = (SSLServerSocket)sc.getServerSocketFactory().createServerSocket(this.port);
			socket.setEnabledProtocols(new String[]{"TLSv1","TLSv1.1","TLSv1.2"});
			this.serverSocket = socket;
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to open server port " + this.port + " - " + e.getMessage());
		}
	}
}
