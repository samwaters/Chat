package sam.WSServer.Servers;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import sam.WSServer.Utils;

public class Server extends Thread
{
	private int port;
	private SSLServerSocket serverSocket;
	private boolean canRun = true;
	private Map<String, ServerThread> clients;
	
	public Server(int port)
	{
		this.port = port;
		this.clients = new HashMap<String, ServerThread>();
	}
	
	
	private void createServerSocket()
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
			System.out.println(Arrays.toString(socket.getSupportedCipherSuites()));
			this.serverSocket = socket;
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to open server port " + this.port + " - " + e.getMessage());
		}
	}
	
	public ServerThread getClientThread(String user)
	{
		if(!this.clients.containsKey(user))
		{
			return null;
		}
		ServerThread client = this.clients.get(user);
		if(!client.isAlive())
		{
			this.removeUser(user);
			return null;
		}
		return client;
	}
	
	public void kill()
	{
		this.canRun = false;
		try
		{
			this.serverSocket.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Cannot close server socket!");
		}
	}
	
	public void messageAll(String message)
	{
		for(String user : this.clients.keySet())
		{
			this.messageDirect(user, message);
		}
	}
	
	public void messageAll(byte[] message)
	{
		for(String user : this.clients.keySet())
		{
			this.messageDirect(user, message);
		}
	}
	
	public boolean messageDirect(String user, String message)
	{
		ServerThread client = this.getClientThread(user);
		if(client != null)
		{
			return client.sendMessage(message);
		}
		return false;
	}
	
	public boolean messageDirect(String user, byte[] message)
	{
		ServerThread client = this.getClientThread(user);
		if(client != null)
		{
			return client.sendMessage(message);
		}
		return false;
	}
	
	public boolean renameUser(String oldName, String newName)
	{
		if(this.clients.containsKey(oldName) && !this.clients.containsKey(newName))
		{
			ServerThread client = this.clients.get(oldName);
			this.clients.remove(oldName);
			this.clients.put(newName, client);
			return true;
		}
		return false;
	}
	
	public boolean removeUser(String user)
	{
		if(this.clients.containsKey(user))
		{
			this.clients.remove(user);
			return true;
		}
		return false;
	}
	
	public void run()
	{
		createServerSocket();
		while(this.canRun)
		{
			Socket clientSocket = null;
			try
			{
				clientSocket = this.serverSocket.accept();
				Utils.logMessage("Client connected: " + clientSocket.getInetAddress().toString());
				String threadName = "WS_" + Calendar.getInstance().getTimeInMillis();
				ServerThread s = new ServerThread(this, clientSocket, threadName);
				s.start();
				this.clients.put("" + s.getId(), s);
			}
			catch(IOException e)
			{
				Utils.logMessage("Failed to accept connection: " + e.getMessage());
			}
		}
	}
}
