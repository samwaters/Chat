package sam.WSServer.Responders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HTTPResponder
{
	private HashMap<String, String> _headers = new HashMap<String, String>();
	
	public HTTPResponder()
	{
		
	}
	
	public void addDefaultHeaders()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		this.addHeader("Date", sdf.format(new Date()));
		this.addHeader("Last-Modified", sdf.format(new Date()));
		this.addHeader("Connection", "Close");
		this.addHeader("Content-Type", "text/html");
		this.addHeader("Server", "Chat/1.0");
		this.addHeader("Accept-Ranges", "bytes");
	}
	
	public void addHeader(String header, String value)
	{
		this._headers.put(header, value);
	}
	
	public void removeHeader(String header)
	{
		if(this._headers.containsKey(header))
		{
			this._headers.remove(header);
		}
	}
	
	public String respond(String body)
	{
		return this.respond("HTTP/1.1 200 OK", body);
	}
	
	public String respond(String httpCode, String body)
	{
		String response = httpCode + "\r\n";
		for(HashMap.Entry<String, String> entry : this._headers.entrySet())
		{
			response += entry.getKey() + ": " + entry.getValue() + "\r\n";
		}
		if(body != null && !body.equals(""))
		{
			response += "Content-Length: " + body.length() + "\r\n\r\n";
			return response + body;
		}
		else
		{
			return response + "\r\n";
		}
	}
}
