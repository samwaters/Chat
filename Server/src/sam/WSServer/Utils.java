package sam.WSServer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Utils
{
	public static void logMessage(String message)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String timeStamp = sdf.format(new Date());
		System.out.println("[" + timeStamp + "] " + message);
	}
	
	public static String padLeft(String input, int desiredLength, char padCharacter)
	{
		return Utils.padString(input, desiredLength, padCharacter, false);
	}
	
	public static String padRight(String input, int desiredLength, char padCharacter)
	{
		return Utils.padString(input, desiredLength, padCharacter, true);
	}
	
	public static String padString(String input, int desiredLength, char padCharacter, boolean padRight)
	{
		int fillLength = desiredLength - input.length();
		if(fillLength <= 0)
		{
			return input;
		}
		char[] fillArray = new char[fillLength];
		Arrays.fill(fillArray, padCharacter);
		if(padRight)
		{
			return input + new String(fillArray);
		}
		else
		{
			return new String(fillArray) + input;
		}
	}
}
