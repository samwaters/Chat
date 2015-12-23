package sam.WSServer.Configuration;

public class Configuration
{
	//General
	public static final String LineSeparator = "\r\n";
	//HTTP Stuff
	public static final String[] HTTPRequestRequiredParameters = {"UPGRADE", "CONNECTION", "SEC-WEBSOCKET-KEY"};
	public static final String[] HTTPResponseRequiredParameters = {"UPGRADE", "CONNECTION", "SEC-WEBSOCKET-KEY"};
	//Websocket Stuff
	public static final String WebsocketGUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
}
