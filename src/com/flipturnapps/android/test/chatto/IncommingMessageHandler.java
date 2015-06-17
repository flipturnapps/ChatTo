package com.flipturnapps.android.test.chatto;

import org.jasypt.util.text.BasicTextEncryptor;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class IncommingMessageHandler
{

	private BasicTextEncryptor encryptor;
	private static String phoneNumber;
	
	public void handleNewMessage(String source, String body, Context context, Intent intent) 
	{
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
	
	public static String getPhoneNumber() {
		return phoneNumber;
	}
	public static void setPhoneNumber(String phoneNumber) {
		IncommingMessageHandler.phoneNumber = phoneNumber;
	}

}
