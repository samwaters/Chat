package sam.WSServer.Messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import sam.WSServer.Utils;
import sam.WSServer.Enums.WebsocketOpcodes;

public class WebsocketFrame
{
	private ByteArrayOutputStream buffer;
	private byte[] decodedMessage;
	private byte[] encodedMessage;
	private boolean isFinalFrame;
	private boolean isMasked;
	private long payloadLength = -1;
	private byte[] maskKey = null;
	private WebsocketOpcodes opCode;
	private boolean isProcessed = false;
	
	public WebsocketFrame()
	{
		this.buffer = new ByteArrayOutputStream();
	}
	
	public byte[] addData(byte[] data)
	{
		if(this.isProcessed)
		{
			Utils.logMessage("WARNING: Trying to add to a processed frame");
			return null;
		}
		this.buffer.write(data, 0, data.length);
		if(this.decodeMessage())
		{
			if(this.buffer.size() > this.getTotalMessageLength())
			{
				//Got more data than we needed, return what we don't need
				byte[] payload = this.buffer.toByteArray();
				this.buffer.reset();
				//[0,1,2,3,4,5]
				return Arrays.copyOfRange(payload, (int)this.getTotalMessageLength(), payload.length);
			}
			else
			{
				//Exactly the right buffer size
				this.buffer.reset();
				return null;
			}
		}
		else
		{
			//Couldn't process, either due to insufficient or invalid data 
			if(this.buffer.size() > 2)
			{
				if(this.validatePayload())
				{
					return null;
				}
				else
				{
					Utils.logMessage("WARNING: Stripping first byte");
					//Invalid first byte!
					byte[] payload = this.buffer.toByteArray();
					return Arrays.copyOfRange(payload, 1, payload.length);
				}
			}
			else
			{
				//Not enough data
				return null;
			}
		}
	}
	
	public boolean createMaskedMessage(boolean finalFrame, byte[] maskKey, WebsocketOpcodes opCode, byte[] message)
	{
		if(this.isProcessed)
		{
			return false;
		}
		this.isFinalFrame = finalFrame;
		this.isMasked = true;
		this.maskKey = maskKey;
		this.opCode = opCode;
		this.decodedMessage = message;
		return this.encodeMessage();
	}
	
	public boolean createMaskedMessage(boolean finalFrame, byte[] maskKey, WebsocketOpcodes opCode, String message)
	{
		if(this.isProcessed)
		{
			return false;
		}
		this.isFinalFrame = finalFrame;
		this.isMasked = true;
		this.maskKey = maskKey;
		this.opCode = opCode;
		this.decodedMessage = message.getBytes();
		return this.encodeMessage();
	}
	
	public boolean createUnmaskedMessage(boolean finalFrame, WebsocketOpcodes opCode, byte[] message)
	{
		if(this.isProcessed)
		{
			return false;
		}
		this.isFinalFrame = finalFrame;
		this.isMasked = false;
		this.maskKey = null;
		this.opCode = opCode;
		this.decodedMessage = message;
		return this.encodeMessage();
	}
	
	public boolean createUnmaskedMessage(boolean finalFrame, WebsocketOpcodes opCode, String message)
	{
		if(this.isProcessed)
		{
			return false;
		}
		this.isFinalFrame = finalFrame;
		this.isMasked = false;
		this.maskKey = null;
		this.opCode = opCode;
		this.decodedMessage = message.getBytes();
		return this.encodeMessage();
	}
	
	private boolean decodeMessage()
	{
		if(this.buffer.size() < 2)
		{
			return false;
		}
		byte[] payload = this.buffer.toByteArray();
		this.isFinalFrame = ((payload[0] & 128) >>> 7 == 1);
		this.opCode = this.translateOpcode(payload[0] & 15);
		this.isMasked = ((payload[1] & 128) >>> 7 == 1);
		this.payloadLength = Byte.toUnsignedInt((byte)(payload[1] & 127));
		int nextOffset = 2;
		if(this.payloadLength == 126 || this.payloadLength == 127)
		{
			//2 or 8 bytes of extra length data
			int requiredByteCount = this.payloadLength == 126 ? 2 : 8;
			if(payload.length < requiredByteCount + 2)
			{
				return false;
			}
			long length = 0;
			for(int i=0; i<requiredByteCount; i++)
			{
				/*
				 * Bytes are in most significant byte first order
				 * Iterate over them and shift them by the required amount (there will be 2 bytes if the initial length is 126, 8 otherwise)
				 * When there are 2 bytes, byte[0] needs to be shifted by 8 and byte[1] does not need to be shifted
				 * When there are 8 bytes, byte[0] needs to be shifted by 56, byte[1] by 48, byte[2] by 40 etc
				 */
				int curByte = Byte.toUnsignedInt(payload[2 + i]);
				int shiftAmount = ((requiredByteCount - 1) - i) * 8;
				//int shiftedValue = curByte << shiftAmount; //Uncomment this for debug
				length += curByte << shiftAmount;
			}
			nextOffset += requiredByteCount;
			this.payloadLength = length;
		}
		if(this.isMasked)
		{
			if(payload.length < nextOffset + 4)
			{
				//Not got enough data yet
				return false;
			}
			this.maskKey = new byte[4];
			System.arraycopy(payload, nextOffset, this.maskKey, 0, 4);
			nextOffset += 4;
		}
		if(payload.length < nextOffset + this.payloadLength)
		{
			//Not got enough data yet
			return false;
		}
		byte[] messageData = Arrays.copyOfRange(payload, nextOffset, nextOffset + (int)this.payloadLength);
		if(this.isMasked)
		{
			//Decode the data with the mask
			for(int i=0; i<messageData.length; i++)
			{
				messageData[i] = (byte)(messageData[i] ^ this.maskKey[i % 4]);
			}
		}
		this.decodedMessage = messageData;
		this.isProcessed = true;
		return true;
	}
	
	private boolean encodeMessage()
	{
		this.payloadLength = (this.decodedMessage != null) ? this.decodedMessage.length : 0;
		ByteArrayOutputStream encoded = new ByteArrayOutputStream();
		//Fin, RSV, Opcode
		int isFinal = (this.isFinalFrame) ? 1 : 0;
		encoded.write((isFinal << 7) | this.translateOpcode(this.opCode));
		int hasMask = (this.isMasked) ? 1 : 0;
		int payloadLength;
		if(this.payloadLength > 65536)
		{
			payloadLength = 127;
		}
		else if(this.payloadLength > 125)
		{
			payloadLength = 126;
		}
		else
		{
			payloadLength = (int)this.payloadLength; //This cast is fine, as it will only ever be up to 127
		}
		encoded.write((hasMask << 7) | payloadLength);
		if(payloadLength == 126 || payloadLength == 127)
		{
			int byteCount = payloadLength == 126 ? 2 : 8;
			byte[] payloadLengthBytes = new byte[byteCount];
			for(int i=0; i< byteCount; i++)
			{
				int shiftAmount = ((byteCount - 1) - i) * 8;
				long shiftedValue = this.payloadLength >>> shiftAmount;
				payloadLengthBytes[i] = (byte)shiftedValue;
			}
			try
			{
				encoded.write(payloadLengthBytes);
			}
			catch (IOException e)
			{
				Utils.logMessage("IOException in encodeMessage:" + e.getMessage());
			}
		}
		if(this.isMasked)
		{
			encoded.write(this.maskKey, 0, 4);
		}
		try
		{
			encoded.write(this.decodedMessage);
		}
		catch(IOException e)
		{
			Utils.logMessage("IOException in encodeMessage:" + e.getMessage());
		}
		this.encodedMessage = encoded.toByteArray();
		this.isProcessed = true;
		return true;
	}
	
	public boolean getIsFinalFrame()
	{
		return this.isFinalFrame;
	}
	
	public boolean getIsMasked()
	{
		return this.isMasked;
	}
	
	public byte[] getMaskKey()
	{
		return this.isProcessed ? this.maskKey : null;
	}
	
	public WebsocketOpcodes getOpCode()
	{
		return this.isProcessed ? this.opCode : null;
	}
	
	public byte[] getDecodedMessage()
	{
		return this.isProcessed ? this.decodedMessage : null;
	}
	
	public String getDecodedMessageString()
	{
		return this.isProcessed ? new String(this.decodedMessage) : "";
	}
	
	public byte[] getEncodedMessage()
	{
		return this.isProcessed ? this.encodedMessage : null;
	}
	
	public String getEncodedMessageString()
	{
		return this.isProcessed ? new String(this.encodedMessage) : "";
	}
	
	/**
	 * Get the entire message length, including all header bytes
	 * @return long
	 */
	public long getTotalMessageLength()
	{
		if(!this.isProcessed)
		{
			return -1;
		}
		long total = 2;
		if(this.payloadLength > 125)
		{
			total += this.payloadLength < 65536 ? 2 : 8;
		}
		total += this.payloadLength;
		if(this.isMasked)
		{
			total += 4;
		}
		return total;
	}
	
	public boolean isProcessed()
	{
		return this.isProcessed;
	}
	
	/**
	 * Translate integer to opCode
	 * @param opCode The integer opCode to translate
	 * @return WebsocketOpcodes
	 */
	public WebsocketOpcodes translateOpcode(int opCode)
	{
		switch(opCode)
		{
			case 0:
				return WebsocketOpcodes.CONTINUATION;
			case 1:
				return WebsocketOpcodes.TEXT;
			case 2:
				return WebsocketOpcodes.BINARY;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return WebsocketOpcodes.NONCONTROL_RESERVED;
			case 8:
				return WebsocketOpcodes.CLOSE;
			case 9:
				return WebsocketOpcodes.PING;
			case 10:
				return WebsocketOpcodes.PONG;
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				return WebsocketOpcodes.CONTROL_RESERVED;
			default:
				return WebsocketOpcodes.INVALID;
		}
	}
	
	/**
	 * Translate opCode to integer
	 * @param opCode The websocket opCode to translate
	 * @return int
	 */
	public int translateOpcode(WebsocketOpcodes opCode)
	{
		switch(opCode)
		{
		case BINARY:
			return 2;
		case CLOSE:
			return 8;
		case CONTINUATION:
			return 0;
		case CONTROL_RESERVED:
			return 11;
		case INVALID:
			return -1;
		case NONCONTROL_RESERVED:
			return 3;
		case PING:
			return 9;
		case PONG:
			return 10;
		case TEXT:
			return 1;
		default:
			return 1;
		}
	}
	
	private boolean validatePayload()
	{
		if(this.buffer.size() < 2)
		{
			return false;
		}
		byte[] payload = this.buffer.toByteArray();
		//Make sure all RSV bytes are off
		int rsv1 = (payload[0] & 64) >> 6;
		int rsv2 = (payload[0] & 32) >> 5;
		int rsv3 = (payload[0] & 16) >> 4;
		if(rsv1 == 1 || rsv2 == 1 || rsv3 == 1)
		{
			return false;
		}
		//Make sure the payload length is valid
		byte payloadLength = (byte)(payload[1] & 127);
		return Byte.toUnsignedInt(payloadLength) >= 0;
	}
}
