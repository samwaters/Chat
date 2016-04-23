package sam.WSServer.Messages;

import java.util.ArrayList;

import sam.WSServer.Enums.ConnectionType;
import sam.WSServer.Enums.MessageType;
import sam.WSServer.Processors.HTTPProcessor;
import sam.WSServer.Processors.IProcessor;
import sam.WSServer.Processors.UpgradeProcessor;
import sam.WSServer.Processors.WebsocketProcessor;
import sam.WSServer.Servers.ServerThread;

public class Message
{
	private WebsocketFrame _currentFrame;
	private String _httpBuffer;
	private boolean _isComplete = false;
	private MessageType _messageType;
	private ServerThread _thread;
	private ArrayList<WebsocketFrame> _websocketBuffer;
	
	public Message(ServerThread thread)
	{
		this._messageType = MessageType.UNKNOWN;
		this._thread = thread;
		if(thread.getConnectionType() == ConnectionType.HTTP)
		{
			this._httpBuffer = "";
		}
		else
		{
			this._currentFrame = new WebsocketFrame();
			this._websocketBuffer = new ArrayList<WebsocketFrame>();
		}
	}
	
	public byte[] addData(byte[] data)
	{
		if(this._isComplete)
		{
			return data;
		}
		if(this._thread.getConnectionType() == ConnectionType.HTTP)
		{
			/*
			 * Non-standard requirement: the GET request must be <= 4096b (the size we read in ServerThread)
			 * As such, we won't consider anything over this to be part of the same request and will respond to it as if it's a new request
			 * This might create issues for non-compliant clients if they send large HTTP requests or multiple
			 * But that's their problem - we'll handle and reject those requests
			 */
			this._httpBuffer += new String(data, 0, data.length);
			this._isComplete = true;
		}
		else
		{
			/*
			 * The WebsocketFrame class takes the amount of data it needs and returns anything extra
			 * So if we get a non-null response, we can assume it's for a new frame
			 * If the last frame is flagged as final, we can return this data back to the thread for a new request
			 */
			while(data != null)
			{
				data = this._currentFrame.addData(data);
				if(this._currentFrame.isProcessed())
				{
					_websocketBuffer.add(this._currentFrame);
					if(this._currentFrame.getIsFinalFrame())
					{
						this._isComplete = true;
						return data; //Can be null if we have exactly the right amount of data
					}
					this._currentFrame = new WebsocketFrame();
				}
			}
		}
		return null;
	}
	
	public String getDecoded()
	{
		if(this._isComplete)
		{
			if(this._thread.getConnectionType() == ConnectionType.HTTP)
			{
				return this._httpBuffer;
			}
			else
			{
				String decoded = "";
				for(WebsocketFrame frame : this._websocketBuffer)
				{
					decoded += frame.getDecodedMessageString();
				}
				return decoded;
			}
		}
		return null;
	}
	
	public String getHTTPBuffer()
	{
		return this._thread.getConnectionType() == ConnectionType.HTTP ? this._httpBuffer : null;
	}
	
	public boolean getIsComplete()
	{
		return this._isComplete;
	}
	
	public MessageType getMessageType()
	{
		return this._messageType;
	}
	
	public IProcessor getProcessor()
	{
		IProcessor processor;
		if(this._thread.getConnectionType() == ConnectionType.HTTP)
		{
			//Is this a HTTP or UPGRADE request?
			UpgradeProcessor uP = new UpgradeProcessor();
			uP.setMessage(this);
			if(uP.decode())
			{
				processor = uP;
			}
			else
			{
				processor = new HTTPProcessor();
			}
		}
		else
		{
			processor = new WebsocketProcessor();
		}
		processor.setMessage(this);
		processor.setThread(this._thread);
		return processor;
	}
	
	public ArrayList<WebsocketFrame> getWebsocketBuffer()
	{
		if(this._thread.getConnectionType() != ConnectionType.WEBSOCKET)
		{
			return null;
		}
		return this._websocketBuffer;
	}
	
	public WebsocketFrame getWebsocketBuffer(int frameIndex)
	{
		if(this._thread.getConnectionType() != ConnectionType.WEBSOCKET)
		{
			return null;
		}
		return this._websocketBuffer.size() >= frameIndex ? this._websocketBuffer.get(frameIndex) : null;
	}
}
