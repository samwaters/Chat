package sam.WSServer.Messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import sam.WSServer.Enums.WebsocketOpcodes;

public class WebsocketMessage extends BaseMessage
{
	private ArrayList<WebsocketFrame> frames;

	public WebsocketMessage()
	{
		this.frames = new ArrayList<WebsocketFrame>();
	}
	
	public boolean addFrame(WebsocketFrame frame)
	{
		if(this.isReady())
		{
			return false;
		}
		this.frames.add(frame);
		return true;
	}
	
	public byte[] getEncodedMessage()
	{
		if(!this.isReady())
		{
			return null;
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for(int i=0; i<this.frames.size(); i++)
		{
			try
			{
				buffer.write(this.frames.get(i).getEncodedMessage());
			}
			catch (IOException e)
			{
			}
		}
		return buffer.toByteArray();
	}
	
	public WebsocketFrame getFrame(int index)
	{
		if(index > this.frames.size())
		{
			return null;
		}
		return this.frames.get(index);
	}
	
	public ArrayList<WebsocketFrame> getFrames()
	{
		return this.frames;
	}
	
	public String getMessage()
	{
		if(!this.isReady())
		{
			return null;
		}
		if(this.frames.get(0).getOpCode() != WebsocketOpcodes.TEXT)
		{
			return "";
		}
		String message = "";
		for(int i=0; i<this.frames.size(); i++)
		{
			message += this.frames.get(i).getDecodedMessageString();
		}
		return message;
	}
	
	public boolean isReady()
	{
		if(this.frames.size() == 0)
		{
			//No frames
			return false;
		}
		for(int i=0; i<this.frames.size(); i++)
		{
			if(!this.frames.get(i).isProcessed())
			{
				//This frame is not processed
				return false;
			}
		}
		if(!this.frames.get(this.frames.size() - 1).getIsFinalFrame())
		{
			//Last frame is not final
			return false;
		}
		return true;
	}
}
