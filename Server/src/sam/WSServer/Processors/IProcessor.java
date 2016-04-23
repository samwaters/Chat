package sam.WSServer.Processors;

import java.util.ArrayList;

import sam.WSServer.Enums.MessageActions;
import sam.WSServer.Messages.Message;
import sam.WSServer.Servers.ServerThread;

public interface IProcessor
{
	public boolean decode();
	public ArrayList<MessageActions> getActions();
	public boolean getIsDecoded();
	public void process();
	public void setMessage(Message m);
	public void setThread(ServerThread t);
}
