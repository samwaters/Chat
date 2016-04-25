package sam.WSServer.Responders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import sam.WSServer.Enums.WebsocketOpcodes;
import sam.WSServer.Messages.WebsocketFrame;
import sam.WSServer.Utils;

public class WebsocketResponder
{
	private ArrayList<WebsocketFrame> frames;

	public WebsocketResponder()
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
		for(WebsocketFrame frame : this.frames)
		{
			try
			{
				buffer.write(frame.getEncodedMessage());
			}
			catch(IOException e)
			{
				Utils.logMessage("Error writing to encoded buffer in WebsocketResponder");
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
		for(WebsocketFrame frame : this.frames)
		{
			message += frame.getDecodedMessageString();
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
		for(WebsocketFrame frame : this.frames)
		{
			if(!frame.isProcessed())
			{
				//This frame is not processed
				return false;
			}
		}
		return this.frames.get(this.frames.size() - 1).getIsFinalFrame(); //Make sure the last frame is final
	}
}
