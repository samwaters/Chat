package sam.WSServer.Messages;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import sam.WSServer.Utils;
import sam.WSServer.Configuration.Configuration;
import sam.WSServer.Enums.HTTPMessageType;
import sam.WSServer.Enums.MessageActions;

public class HTTPMessage extends BaseMessage
{
	private Map<String, String> headers;
	private String body;
	private String messageBuffer;
	private HTTPMessageType messageType;
	private boolean processed;
	private String responseCode;
	
	public HTTPMessage()
	{
		this.body = "";
		this.headers = new HashMap<String, String>();
		this.messageBuffer = "";
		this.messageType = HTTPMessageType.HTTP_REQUEST;
		this.processed = false;
		this.responseCode = "";
	}
	
	public void addHeader(String headerName, String headerValue)
	{
		this.headers.put(headerName.toUpperCase(), headerValue.trim());
	}
	
	public byte[] addData(byte[] message)
	{
		return null;
	}
	
	public void addData(String message)
	{
		if(!this.processed)
		{
			this.messageBuffer += message;
			this.decodeMessage();
		}
	}
	
	public String calculateWebsocketAcceptKey(String challenge)
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
	
	public void decodeMessage()
	{
		//The message is complete, so split it into lines
		String[] parameters = this.messageBuffer.split(Configuration.LineSeparator);
		for(int i=0; i<parameters.length; i++)
		{
			//Split the line up into name and value pairs
			String[] parameterParts = parameters[i].split(":");
			if(parameterParts.length < 2)
			{
				//Ignore this as we should always have at least two parts
				continue;
			}
			this.addHeader(parameterParts[0], parameterParts[1]);
		}
		if(this.isReady())
		{
			this.processed = true;
		}
	}
	
	public String getHeader(String headerName)
	{
		if(!this.headers.containsKey(headerName.toUpperCase()))
		{
			return null;
		}
		return this.headers.get(headerName.toUpperCase());
	}
	
	public String getMessage()
	{
		String message = this.responseCode + Configuration.LineSeparator;
		for(Map.Entry<String, String> data : this.headers.entrySet())
		{
			message += data.getKey() + ": " + data.getValue() + Configuration.LineSeparator;
		}
		if(this.body != "")
		{
			message += this.body + Configuration.LineSeparator;
		}
		message += Configuration.LineSeparator;
		return message;
	}
	
	public ArrayList<MessageActions> getMessageActions()
	{
		ArrayList<MessageActions> actions = new ArrayList<MessageActions>();
		if(this.messageType == HTTPMessageType.HTTP_REQUEST)
		{
			actions.add(MessageActions.UPGRADE_TO_WEBSOCKET);
		}
		return actions;
	}
	
	public boolean isReady()
	{
		if(!this.messageBuffer.endsWith(Configuration.LineSeparator))
		{
			Utils.logMessage("IsReady: No Line Separator");
			return false;
		}
		String[] required = (this.messageType == HTTPMessageType.HTTP_REQUEST) ? Configuration.HTTPRequestRequiredParameters : Configuration.HTTPResponseRequiredParameters;
		for(int i=0; i<required.length; i++)
		{
			if(!this.headers.containsKey(required[i]))
			{
				Utils.logMessage("IsReady: Header " + required[i] + " missing");
				Utils.logMessage("Buffer: " + this.messageBuffer);
				return false;
			}
		}
		return true;
	}
	
	public void setBody(String body)
	{
		this.body = body;
	}
	
	public void setMessageType(HTTPMessageType type)
	{
		this.messageType = type;
	}
	
	public void setResponseCode(String responseCode)
	{
		this.responseCode = responseCode;
	}
}
