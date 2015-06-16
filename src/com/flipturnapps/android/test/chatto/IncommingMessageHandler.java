package com.flipturnapps.android.test.chatto;

import org.jasypt.util.text.BasicTextEncryptor;

import android.content.Context;
import android.content.Intent;

public class IncommingMessageHandler
{

	private BasicTextEncryptor encryptor;

	public void handleNewMessage(String source, String body, Context context, Intent intent) 
	{
		//get password from server
		String password = "!@#$%12345abcde";
		if(encryptor == null)
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
			MainActivity.textViewOutputter.outputText(source + ": See messaging app.");
		}

	}

}
