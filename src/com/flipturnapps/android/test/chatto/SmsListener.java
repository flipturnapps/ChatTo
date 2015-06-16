package com.flipturnapps.android.test.chatto;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsListener extends BroadcastReceiver{

	private IncommingMessageHandler handler;

	@Override
	public void onReceive( Context context,  Intent intent) 
	{

		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
			SmsMessage[] msgs = null;
			String msg_from = null;
			ArrayList<String> messages = new ArrayList<String>();
			if (bundle != null){
				//---retrieve the SMS message received---
				try{
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for(int i=0; i<msgs.length; i++)
					{
						msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
						msg_from = msgs[i].getOriginatingAddress();
						String msgBody = msgs[i].getMessageBody();
						messages.add(msgBody);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(messages.size() > 0)
			{
				if(handler == null)
					handler = new IncommingMessageHandler();
				String combinedMessage = "";
				for(int i = 0 ; i < messages.size(); i++)
				{
					combinedMessage += messages.get(i);
				}
				handler.handleNewMessage(msg_from, combinedMessage, context, intent);
			}
		}
	}
}
