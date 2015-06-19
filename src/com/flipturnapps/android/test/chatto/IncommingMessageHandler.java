package com.flipturnapps.android.test.chatto;

import org.jasypt.util.text.BasicTextEncryptor;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class IncommingMessageHandler
{

	private BasicTextEncryptor encryptor;
	private static String friendNumber;
	private static String phoneNumber;

	public void handleNewMessage(String source, String body, Context context, Intent intent) 
	{
		source = MainActivity.removeNonNumberValues(source);
		if(source.equals(friendNumber))
		{
			source = "Them";
			if(phoneNumber == null)
			{
				TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
				phoneNumber = tMgr.getLine1Number();
			}
			String password = ChatToClient.self.aquireNextResponse(phoneNumber);	


			encryptor = new BasicTextEncryptor();
			try
			{
				encryptor.setPassword(password);
				String toBeDecrypted = body.split("~")[1];
				String decryptedMessage = encryptor.decrypt(toBeDecrypted);
				MainActivity.textViewOutputter.outputText(source + ": " + decryptedMessage);
			}
			catch(Exception ex)
			{
				MainActivity.textViewOutputter.outputText(source + ": <See messaging app.>");
			}
		}
		else
		{
			System.out.println("source mismatch: source:" + source + " friendnum:" + friendNumber);
		}

	}

	public static String getThisPhoneNumber() {
		return phoneNumber;
	}
	public static void setThisPhoneNumber(String phoneNumber) {
		IncommingMessageHandler.phoneNumber = phoneNumber;
	}
	public static String getFriendPhoneNumber() {
		return friendNumber;
	}
	public static void setFriendPhoneNumber(String num) {
		IncommingMessageHandler.friendNumber = num;
	}

}
