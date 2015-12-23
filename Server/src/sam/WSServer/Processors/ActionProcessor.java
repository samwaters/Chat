package sam.WSServer.Processors;

import java.util.ArrayList;

import sam.WSServer.Utils;
import sam.WSServer.Enums.ConnectionType;
import sam.WSServer.Enums.HTTPMessageType;
import sam.WSServer.Enums.MessageActions;
import sam.WSServer.Enums.WebsocketOpcodes;
import sam.WSServer.Messages.BaseMessage;
import sam.WSServer.Messages.HTTPMessage;
import sam.WSServer.Messages.WebsocketFrame;
import sam.WSServer.Messages.WebsocketMessage;
import sam.WSServer.Servers.ServerThread;

public class ActionProcessor
{
	private ServerThread thread;
	
	public ActionProcessor(ServerThread thread)
	{
		this.thread = thread;
	}
	
	public void processAction(MessageActions action, BaseMessage message)
	{
		switch(action)
		{
			case UPGRADE_TO_WEBSOCKET:
				HTTPMessage httpMessage = (HTTPMessage)message;
				this.upgradeToWebsocket(httpMessage.getHeader("SEC-WEBSOCKET-KEY"));
				break;
			case PROCESS_MESSAGE:
				WebsocketMessage websocketMessage = (WebsocketMessage)message;
				this.processMessage(websocketMessage);
				break;
			case CLOSE_CONNECTION:
				this.closeConnection();
				break;
			case RESPOND_TO_PING:
				this.respondToPing();
				break;
			case ACKNOWLEDGE_PONG:
				break;
			default:
				Utils.logMessage("Unknown action type: " + action.toString());	
		}
	}
	
	private void closeConnection()
	{
		Utils.logMessage("Closing connection");
		WebsocketFrame closeFrame = new WebsocketFrame();
		closeFrame.createUnmaskedMessage(true, WebsocketOpcodes.CLOSE, "");
		WebsocketMessage closeMessage = new WebsocketMessage();
		closeMessage.addFrame(closeFrame);
		this.thread.sendMessage(closeMessage.getEncodedMessage());
		this.thread.kill();
	}
	
	private void processMessage(WebsocketMessage receivedMessage)
	{
		Utils.logMessage("Responding to received message");
		//Utils.logMessage(receivedMessage.getMessage());
		WebsocketMessage replyMessage = new WebsocketMessage();
		ArrayList<WebsocketFrame> frames = receivedMessage.getFrames();
		for(int i=0; i<frames.size(); i++)
		{
			WebsocketFrame thisFrame = receivedMessage.getFrame(i);
			//String message = "REPLAY: " + thisFrame.getDecodedMessageString();
			WebsocketFrame replyFrame = new WebsocketFrame();
			replyFrame.createUnmaskedMessage(
				thisFrame.getIsFinalFrame(),
				i == 0 ? thisFrame.getOpCode() : WebsocketOpcodes.CONTINUATION,
				thisFrame.getDecodedMessageString()
			);
			replyMessage.addFrame(replyFrame);
		}
		Utils.logMessage("About to send...");
		this.thread.sendMessage(replyMessage.getEncodedMessage());
	}
	
	private void respondToPing()
	{
		Utils.logMessage("Responding to ping");
		WebsocketFrame pongFrame = new WebsocketFrame();
		pongFrame.createUnmaskedMessage(true, WebsocketOpcodes.PONG, "");
		WebsocketMessage pongMessage = new WebsocketMessage();
		pongMessage.addFrame(pongFrame);
		this.thread.sendMessage(pongMessage.getEncodedMessage());
	}
	
	private void upgradeToWebsocket(String challengeKey)
	{
		Utils.logMessage("Upgrading to websocket");
		HTTPMessage response = new HTTPMessage();
		response.setMessageType(HTTPMessageType.HTTP_RESPONSE);
		response.setResponseCode("HTTP/1.1 101 Switching Protocols");
		response.addHeader("Upgrade", "websocket");
		response.addHeader("Connection", "Upgrade");
		response.addHeader("Sec-Websocket-Accept", response.calculateWebsocketAcceptKey(challengeKey));
		this.thread.sendMessage(response.getMessage());
		this.thread.setConnectionType(ConnectionType.WEBSOCKET);
	}
}
