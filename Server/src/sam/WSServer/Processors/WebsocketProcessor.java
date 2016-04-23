package sam.WSServer.Processors;

import java.util.ArrayList;

import sam.WSServer.Utils;
import sam.WSServer.Enums.MessageActions;
import sam.WSServer.Enums.WebsocketOpcodes;
import sam.WSServer.Messages.Message;
import sam.WSServer.Messages.WebsocketFrame;
import sam.WSServer.Responders.WebsocketResponder;
import sam.WSServer.Servers.ServerThread;

public class WebsocketProcessor implements IProcessor
{
	private boolean _decoded = false;
	private Message _message;
	private ServerThread _thread;
	
	private void _acknowledgePong()
	{
		Utils.logMessage("Got PONG!");
	}
	
	private void _closeConnection()
	{
		WebsocketResponder wR = new WebsocketResponder();
		WebsocketFrame closeFrame = new WebsocketFrame();
		closeFrame.createUnmaskedMessage(true, WebsocketOpcodes.CLOSE, "");
		wR.addFrame(closeFrame);
		this._thread.sendMessage(wR.getEncodedMessage());
		this._thread.kill();
	}
	
	private void _respondToMessage()
	{
		WebsocketResponder wR = new WebsocketResponder();
		WebsocketFrame closeFrame = new WebsocketFrame();
		closeFrame.createUnmaskedMessage(true, WebsocketOpcodes.TEXT, "Thanks for your message of " + this._message.getDecoded() + "!");
		wR.addFrame(closeFrame);
		this._thread.sendMessage(wR.getEncodedMessage());
	}
	
	private void _respondToPing()
	{
		WebsocketResponder wR = new WebsocketResponder();
		WebsocketFrame pongFrame = new WebsocketFrame();
		pongFrame.createUnmaskedMessage(true, WebsocketOpcodes.PONG, "");
		wR.addFrame(pongFrame);
		this._thread.sendMessage(wR.getEncodedMessage());
	}
	
	public boolean decode()
	{
		if(!this._decoded && this._message != null && this._message.getIsComplete())
		{
			this._decoded = true;
			return true;
		}
		return false;
	}
	
	public ArrayList<MessageActions> getActions()
	{
		ArrayList<MessageActions> actions = new ArrayList<MessageActions>();
		switch(this._message.getWebsocketBuffer(0).getOpCode())
		{
		case BINARY:
		case TEXT:
			actions.add(MessageActions.PROCESS_MESSAGE);
			break;
		case CLOSE:
			actions.add(MessageActions.CLOSE_CONNECTION);
			break;
		case PING:
			actions.add(MessageActions.RESPOND_TO_PING);
			break;
		case PONG:
			actions.add(MessageActions.ACKNOWLEDGE_PONG);
			break;
		default:
			Utils.logMessage("Unsupported OpCode " + this._message.getWebsocketBuffer(0).getOpCode() + " in WebsocketProcessor");
			break;
		}
		return actions;
	}
	
	public boolean getIsDecoded()
	{
		return this._decoded;
	}
	
	public void process()
	{
		if(this._decoded)
		{
			for(MessageActions action : this.getActions())
			{
				switch(action)
				{
					case PROCESS_MESSAGE:
						this._respondToMessage();
						break;
					case CLOSE_CONNECTION:
						this._closeConnection();
						break;
					case RESPOND_TO_PING:
						this._respondToPing();
						break;
					case ACKNOWLEDGE_PONG:
						this._acknowledgePong();
						break;
					default:
						Utils.logMessage("Unknown action " + action + " on WebsocketProcessor");	
				}
			}
		}
		
	}
	
	public void setMessage(Message m)
	{
		this._message = m;
	}
	
	public void setThread(ServerThread t)
	{
		this._thread = t;
	}
}
