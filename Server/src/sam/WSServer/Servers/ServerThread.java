package sam.WSServer.Servers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sam.WSServer.Utils;
import sam.WSServer.Enums.ConnectionType;
import sam.WSServer.Messages.BaseMessage;
import sam.WSServer.Messages.HTTPMessage;
import sam.WSServer.Messages.WebsocketFrame;
import sam.WSServer.Messages.WebsocketMessage;
import sam.WSServer.Processors.ActionProcessor;


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
	
	public byte[] getBinaryMessage() throws Exception
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
			return null;
		}
	}
	
	public ConnectionType getConnectionType()
	{
		return this.connectionType;
	}
	
	public String getTextMessage() throws Exception
	{
		if(this.socket.isClosed())
		{
			Utils.logMessage("Terminating thread: Socket closed");
			this.kill();
		}
		byte[] buffer = new byte[4096];
		try
		{
			int read = this.socketInput.read(buffer, 0, 4096);
			if(read <= 0)
			{
				throw new Exception("Could not read text data");
			}
			return new String(buffer, 0, read);
		}
		catch(IOException e)
		{
			Utils.logMessage("getMessage IO Exception: " + e.getMessage());
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
		BaseMessage request = null;
		WebsocketFrame frame = new WebsocketFrame();
		ActionProcessor actionProcessor = new ActionProcessor(this);
		while(this.canRun)
		{
			try
			{
				this.socketInput = this.socket.getInputStream();
				this.socketOutput = this.socket.getOutputStream();
				//Get some data and add it to the request
				if(this.connectionType == ConnectionType.HTTP)
				{
					if(request == null || !(request instanceof HTTPMessage))
					{
						request = new HTTPMessage();
					}
					String textData = this.getTextMessage();
					((HTTPMessage) request).addData(textData);
				}
				else
				{
					if(request == null || !(request instanceof WebsocketMessage))
					{
						request = new WebsocketMessage();
					}
					byte[] binaryData = this.getBinaryMessage();
					while(binaryData != null)
					{
						binaryData = frame.addData(binaryData);
						if(frame.isProcessed())
						{
							((WebsocketMessage) request).addFrame(frame);
							frame = new WebsocketFrame();
						}
					}
				}
				if(request.canProcess())
				{
					actionProcessor.processAction(request.getMessageAction(), request);
					request = null;
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
				Utils.logMessage("Sending byte message " + message.length);
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
		if(this.socket.isConnected())
		{
			try
			{
				Utils.logMessage("Sending message:" + message);
				this.socketOutput.write(message.getBytes());
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
	
	public boolean setConnectionType(ConnectionType connectionType)
	{
		if(this.connectionType == ConnectionType.WEBSOCKET && connectionType == ConnectionType.HTTP)
		{
			//Can't downgrade
			return false;
		}
		this.connectionType = connectionType;
		return true;
	}
}
