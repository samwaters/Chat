package sam.WSServer.Messages;

import sam.WSServer.Enums.MessageActions;

public abstract class BaseMessage
{
	public boolean canProcess()
	{
		if(this instanceof HTTPMessage)
		{
			return ((HTTPMessage) this).isReady();
		}
		else
		{
			return ((WebsocketMessage) this).isReady();
		}
	}
	
	public MessageActions getMessageAction()
	{
		if(this instanceof HTTPMessage)
		{
			return MessageActions.UPGRADE_TO_WEBSOCKET;
		}
		else
		{
			switch(((WebsocketMessage) this).getFrame(0).getOpCode())
			{
				case BINARY:
				case TEXT:
					return MessageActions.PROCESS_MESSAGE;
				case CLOSE:
					return MessageActions.CLOSE_CONNECTION;
				case PING:
					return MessageActions.RESPOND_TO_PING;
				case PONG:
					return MessageActions.ACKNOWLEDGE_PONG;
				default:
					return null;
			}
		}
	}
}
