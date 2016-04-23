package sam.WSServer.Processors;

import java.util.ArrayList;

import sam.WSServer.Utils;
import sam.WSServer.Enums.MessageActions;
import sam.WSServer.Messages.Message;
import sam.WSServer.Responders.HTTPResponder;
import sam.WSServer.Servers.ServerThread;

public class HTTPProcessor implements IProcessor
{
	private boolean _decoded = false;
	private Message _message;
	private ServerThread _thread;
	
	private void _sendHTTPResponse()
	{
		HTTPResponder hR = new HTTPResponder();
		hR.addDefaultHeaders();
		this._thread.sendMessage(hR.respond("<h1>Chat Server</h1><p>This server does not support HTTP(s) connections"));
	}
	
	public boolean decode()
	{
		this._decoded = true;
		return true;
	}
	
	public ArrayList<MessageActions> getActions()
	{
		ArrayList<MessageActions> actions = new ArrayList<MessageActions>();
		actions.add(MessageActions.PROCESS_HTTP);
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
					case PROCESS_HTTP:
						this._sendHTTPResponse();
						this._thread.kill();
						break;
					default:
						Utils.logMessage("Unknown action " + action + " in HTTPProcessor");
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
