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

public class MainActivity extends Activity implements Runnable
{

	private ChatToClient client;
	private TextOutputter toastOutputter;
	private Thread thread;
	private BasicTextEncryptor encryptor;

	static TextViewOutputter textViewOutputter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}


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
			output("Settings are unavailible in this version.");
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


		toastOutputter = new ToastOutputter(this);
		textViewOutputter = new TextViewOutputter(this);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}

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
				findViewById(R.id.button1).setOnClickListener(buttonListener);
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
							client = new ChatToClient(toastOutputter);
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
			public void run() 
			{


				EditText editText = (EditText) findViewById(R.id.phoneNumberField);
				String phoneNum = editText.getText().toString();
				EditText et = (EditText) findViewById(R.id.messageField);
				String message = et.getText().toString();
				for(int x = 0; x < phoneNum.length(); x++)
				{
					boolean charGood = false;
					char ch = phoneNum.charAt(x);
					for(int y = 0; y < 10; y++)
					{
						if((ch + "").equals(y+""))
							charGood = true;
					}
					if(!charGood)
					{
						phoneNum= phoneNum.substring(0,x) + phoneNum.substring(x+1,phoneNum.length()); 
						x--;
					}
				}
				if(message.equalsIgnoreCase("regen"))
				{
					if(client == null)
					{
						output("No connection to server.");
						return;
					}
					client.sendText("regen:" + phoneNum+ ":" + IncommingMessageHandler.getPhoneNumber());					
					clearMessageField();
					return;
				}
				if(message.equalsIgnoreCase("setnum"))
				{
					IncommingMessageHandler.setPhoneNumber(phoneNum);
					clearMessageField();
					clearPhoneNumField();
					return;
				}
				if(client == null)
				{
					output("Encrypted send failed: no server connection.");
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
				textViewOutputter.outputText("You: " + message);
				clearMessageField();

			}



		};
		Thread sendSMSThread = new Thread(run);
		sendSMSThread.start();

	}
	private void onServerFailue() 
	{
		output("Server failure.");
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
				EditText et = (EditText) findViewById(R.id.messageField);
				et.setText("");

			}
		});
	}
	private void clearPhoneNumField()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{			
				EditText et = (EditText) findViewById(R.id.phoneNumberField);
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
		output("Message sent.");

	}
	void output(String s)
	{
		System.out.println("Toasted: " + s);
		toastOutputter.outputText(s);
	}


}
