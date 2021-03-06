package com.flipturnapps.android.test.chatto;

import java.io.IOException;
import java.util.ArrayList;

import org.jasypt.util.text.BasicTextEncryptor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable
{

	private ChatToClient client;
	private Thread thread;
	private BasicTextEncryptor encryptor;
	private String phoneNum;
	private String contactName;
	static TextViewOutputter textViewOutputter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		phoneNum = this.getIntent().getExtras().getString(StartActivity.CONTACT_PHONENUM_EXTRA);
		contactName = this.getIntent().getExtras().getString(StartActivity.CONTACT_NAME_EXTRA);
		phoneNum = removeNonNumberValues(phoneNum);
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}

	}
	public static String removeNonNumberValues(String s)
	{
		for(int x = 0; x < s.length(); x++)
		{
			boolean charGood = false;
			char ch = s.charAt(x);
			for(int y = 0; y < 10; y++)
			{
				if((ch + "").equals(y+""))
					charGood = true;
			}
			if(!charGood)
			{
				s= s.substring(0,x) + s.substring(x+1,s.length()); 
				x--;
			}
		}
		return s;
	}
	@Override
	public void onBackPressed()
	{
		System.exit(-1);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			toast("Settings are unavailible in this version.",false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment 
	{

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	MainActivity getActivity()
	{
		return this;
	}
	@Override
	public void run()
	{
		textViewOutputter = new TextViewOutputter(this);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		IncommingMessageHandler.setFriendPhoneNumber(this.phoneNum);
		final View.OnClickListener buttonListener = new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				onFieldConfirm();

			}
		};
		this.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				findViewById(R.id.button_sendMessage).setOnClickListener(buttonListener);
				textViewOutputter.outputText("Messaging with " + contactName + ":", R.color.text_color_send);
			}

		});
		Runnable connectToServerRunner = new Runnable()
		{
			@Override
			public void run() 
			{
				while (true)
				{
					if(client == null)
					{
						try 
						{
							client = new ChatToClient(getActivity());
						} catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
					try 
					{
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}		
		};
		Thread connectToServerThread = new Thread(connectToServerRunner);
		connectToServerThread.start();
	}
	public void onFieldConfirm()
	{

		Runnable run = new Runnable()
		{
			@Override
			public void run() 
			{

				EditText et = (EditText) findViewById(R.id.editText_messageWriting);
				String message = et.getText().toString();

				if(message.equalsIgnoreCase("regen"))
				{
					if(client == null)
					{
						toast("No connection to server.",true);
						return;
					}
					client.sendText("regen:" + phoneNum+ ":" + IncommingMessageHandler.getThisPhoneNumber());					
					clearMessageField();
					return;
				}
				if(message.startsWith("snm"))
				{
					try
					{
						IncommingMessageHandler.setThisPhoneNumber(message.split(":")[1]);
					}
					catch(Exception ex)
					{

					}
					clearMessageField();
					return;
				}
				if(client == null)
				{
					toast("Encrypted send failed: no server connection.",true);
					return;
				}
				String password = client.aquireNextResponse(phoneNum);
				if(password == null)
				{
					onServerFailue();
					return;
				}

				encryptor = new BasicTextEncryptor();
				encryptor.setPassword(password);
				String send = "~" + encryptor.encrypt(message) + "~";
				sendSMS(phoneNum,send);
				textViewOutputter.outputText("You   : " + message,R.color.text_color_send);
				clearMessageField();

			}



		};
		Thread sendSMSThread = new Thread(run);
		sendSMSThread.start();

	}
	private void onServerFailue() 
	{
		toast("Server failure.",true);
		try
		{
			client.sendText("close");
			client.close();
		}
		catch(Exception ex)
		{

		}
		client = null;
	}
	private void clearMessageField()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{			
				EditText et = (EditText) findViewById(R.id.editText_messageWriting);
				et.setText("");

			}
		});
	}
	private void sendSMS(String phoneNumber, String message)
	{        
		/*
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
				new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		//---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1)
			{
				 DISABLED EXTRA TOASTS
				switch (getResultCode())
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off", 
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}, new IntentFilter(SENT));

		//---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) 
			{
				 DISABLED EXTRA TOASTS
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered", 
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered", 
							Toast.LENGTH_SHORT).show();
					break;                        
				}

			}
		}, new IntentFilter(DELIVERED));        
		 */



		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(message); 
		sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
		toast("Message sent.",true);

	}
	public void toast(String s, boolean needUI)
	{
		if(!needUI)
			new Toaster(s).run();
		else
			this.runOnUiThread(new Toaster(s));
		System.out.println("Toasted: " + s);
	}
	private class Toaster implements Runnable
	{

		private CharSequence text;

		public Toaster(String s) 
		{
			text = s;
		}

		@Override
		public void run() 
		{
			Toast.makeText(getActivity(),text, Toast.LENGTH_SHORT);			
		}
		
	}


}
