package sam.WSServer.Servers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sam.WSServer.Utils;
import sam.WSServer.Enums.ConnectionType;
import sam.WSServer.Messages.Message;
import sam.WSServer.Processors.IProcessor;


public class ServerThread extends Thread
{
	private Socket socket;
	private InputStream socketInput;
	private OutputStream socketOutput;
	private ConnectionType connectionType = ConnectionType.HTTP;
	private boolean canRun = true;
	private Server server;
	private String threadName;
	
	//Constructor called from Server
	public ServerThread(Server server, Socket socket, String threadName)
	{
		Utils.logMessage("Server Thread started");
		this.server = server;
		this.socket = socket;
		this.threadName = threadName;
	}
	
	public ServerThread(SSLServer server, Socket socket, String threadName)
	{
		
	}
	
	public ConnectionType getConnectionType()
	{
		return this.connectionType;
	}
	
	public byte[] getMessage() throws Exception
	{
		if(this.socket.isClosed())
		{
			Utils.logMessage("Terminating thread: Socket closed");
			this.kill();
			return null;
		}
		byte[] buffer = new byte[4096];
		try
		{
			int read = this.socketInput.read(buffer, 0, 4096);
			if(read <= 0)
			{
				throw new Exception("Could not read binary data");
			}
			byte[] data = new byte[read];
			System.arraycopy(buffer, 0, data, 0, read);
			return data;
		}
		catch(IOException e)
		{
			Utils.logMessage("getMessage IO Exception: " + e.getMessage());
			this.kill();
			return null;
		}
	}
	
	public String getThreadName()
	{
		return this.threadName;
	}
	
	public void kill()
	{
		this.canRun = false;
		try
		{
			this.socket.close();
		}
		catch(IOException e)
		{
			Utils.logMessage("Cannot close thread connection: " + e.getMessage());
		}
	}
	
	public void run()
	{
		/*
		 * Since we read 4096 bytes at a time, there is potential for the data to come in several reads
		 * For this reason, we need to keep the request object until it's complete so we can add data to it
		 * Once it is complete, we can process the response and treat any new data as a new request
		 */
		Message message = new Message(this);
		while(this.canRun)
		{
			try
			{
				this.socketInput = this.socket.getInputStream();
				this.socketOutput = this.socket.getOutputStream();
				//Get some data
				byte[] binaryData = this.getMessage();
				while(binaryData != null)
				{
					binaryData = message.addData(binaryData);
					if(message.getIsComplete())
					{
						IProcessor processor = message.getProcessor();
						if(!processor.getIsDecoded())
						{
							if(!processor.decode())
							{
								Utils.logMessage("Undecodable message in ServerThread!");
							}
						}
						processor.process();
						message = new Message(this);
					}
				}
			}
			catch(IOException e)
			{
				Utils.logMessage("Error reading from network: " + e.getMessage());
			}
			catch(Exception e)
			{
				Utils.logMessage("Error reading data on thread: " + e.getMessage());
				this.kill();
			}
		}
	}
	
	public boolean sendMessage(byte[] message)
	{
		if(this.socket.isConnected())
		{
			try
			{
				this.socketOutput.write(message);
				return true;
			}
			catch(IOException e)
			{
				Utils.logMessage("Failed to send message:" + e.getMessage());
				return false;
			}
		}
		return false;
	}
	
	public boolean sendMessage(String message)
	{
		return this.sendMessage(message.getBytes());
	}
	
	public boolean setConnectionType(ConnectionType connectionType)
	{
		if(this.connectionType == ConnectionType.WEBSOCKET && connectionType == ConnectionType.HTTP)
		{
			return false; //Can't downgrade
		}
		this.connectionType = connectionType;
		return true;
	}
}
