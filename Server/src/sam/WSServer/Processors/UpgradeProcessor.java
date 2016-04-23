package sam.WSServer.Processors;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import sam.WSServer.Utils;
import sam.WSServer.Configuration.Configuration;
import sam.WSServer.Enums.ConnectionType;
import sam.WSServer.Enums.MessageActions;
import sam.WSServer.Messages.Message;
import sam.WSServer.Responders.HTTPResponder;
import sam.WSServer.Servers.ServerThread;

public class UpgradeProcessor implements IProcessor
{
	private boolean _decoded = false;
	private HashMap<String, String> _headers = new HashMap<String, String>();
	private Message _message;
	private ServerThread _thread;
	
	private String _calculateWebsocketAcceptKey(String challenge)
	{
		String keyString = challenge + Configuration.WebsocketGUID;
		try
		{
			//Get the SHA1 of the key string
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(keyString.getBytes());
			byte[] sha1Output = md.digest();
			//Return the Base64 encoded value of the SHA1 output
			return Base64.encode(sha1Output);
		}
		catch(Exception e)
		{
			//Probably NoSuchAlgorithm?
			return "";
		}
	}
	
	private void _upgradeToWebsocket()
	{
		String challengeResponse = this._calculateWebsocketAcceptKey(this._headers.get("SEC-WEBSOCKET-KEY"));
		HTTPResponder hR = new HTTPResponder();
		hR.addHeader("Upgrade", "websocket");
		hR.addHeader("Connection", "Upgrade");
		hR.addHeader("Sec-WebSocket-Accept", challengeResponse);
		//hR.addHeader("Sec-WebSocket-Protocol", "chat");
		this._thread.sendMessage(hR.respond("HTTP/1.1 101 Switching Protocols", null));
		this._thread.setConnectionType(ConnectionType.WEBSOCKET);
	}
	
	public boolean decode()
	{
		if(!this._decoded && this._message != null && this._message.getIsComplete())
		{
			String[] headers = this._message.getHTTPBuffer().split(Configuration.LineSeparator);
			for(String header : headers)
			{
				String[] headerParts = header.split(":");
				if(headerParts.length == 2)
				{
					this._headers.put(headerParts[0].toUpperCase(), headerParts[1].trim());
				}
			}
			//Have we got the required headers?
			for(String requiredHeader : Configuration.HTTPRequestRequiredParameters)
			{
				if(!this._headers.containsKey(requiredHeader))
				{
					this._headers.clear();
					return false;
				}
			}
			this._decoded = true;
			return true;
		}
		return false;
	}
	
	public ArrayList<MessageActions> getActions()
	{
		ArrayList<MessageActions> actions = new ArrayList<MessageActions>();
		actions.add(MessageActions.UPGRADE_TO_WEBSOCKET);
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
					case UPGRADE_TO_WEBSOCKET:
						this._upgradeToWebsocket();
						break;
					default:
						Utils.logMessage("Unknown action " + action + " in UpgradeProcessor");
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
