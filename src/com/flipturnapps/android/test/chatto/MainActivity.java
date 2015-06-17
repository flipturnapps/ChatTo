package com.flipturnapps.android.test.chatto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jasypt.util.text.BasicTextEncryptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable, LocationListener
{
	private static final String SERVERIP = "192.168.2.65";
	private ChatToClient client;
	private TextOutputter toastOutputter;
	private Thread thread;
	private BasicTextEncryptor encryptor;
	private Location lastLocation;
	private static final double DEST_LNG = -123.302629;
	private static final double DEST_LAT = 44.597411;

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
		/* RE-Enable to be client
		//this only attempts to connnect once..... fix 
		try {
			client = new ChatToClient(this.toastOutputter,SERVERIP);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		 */


		this.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				findViewById(R.id.button1).setOnClickListener(buttonListener);
				LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, getActivity());
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, getActivity());
				useLocation(location);
			}

		});






	}
	private String getDistanceOnRoad(double latitude, double longitude, double prelatitute, double prelongitude) 
	{
		String result_in_kms = "";
		String url = "http://maps.google.com/maps/api/directions/xml?origin="
				+ latitude + "," + longitude + "&destination=" + prelatitute
				+ "," + prelongitude + "&sensor=false&units=metric";
		String tag[] = { "text" };
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			response = httpClient.execute(httpPost, localContext);
			InputStream is = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(is);
			if (doc != null) {
				NodeList nl;
				ArrayList<String> args = new ArrayList<String>();
				for (String s : tag) {
					nl = doc.getElementsByTagName(s);
					if (nl.getLength() > 0) {
						Node node = nl.item(nl.getLength() - 1);
						args.add(node.getTextContent());
					} else {
						args.add(" - ");
					}
				}
				result_in_kms = String.format("%s", args.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_in_kms;
	}
	private void useLocation(Location location)
	{
		/* CITYFINDER
		Geocoder gcd = new Geocoder(this.getBaseContext(), Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		String city = null;
		if(addresses != null && addresses.size() > 0)
		{
			city = addresses.get(0).getLocality();

		}
		String s = location.getLongitude() + "\n" + location.getLatitude() + "\n\nMy Current City is: " + city;
		 */
		this.lastLocation = location;

		this.toastOutputter.outputText("loc found");
		//if(client != null)
		//client.sendText(s);
	}

	@Override
	public void onLocationChanged(Location l)
	{
		useLocation(l);

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
	public void onFieldConfirm()
	{
		/* RE-ENABLE TO SEND ENCRYPTED SMS
		//should be in new thread- too lazy right now
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

		String password = client.aquireNextResponse(phoneNum);
		if(encryptor == null)
			encryptor = new BasicTextEncryptor();
		encryptor.setPassword(password);
		String send = "~" + encryptor.encrypt(message) + "~";
		sendSMS(phoneNum,send);
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{			
				EditText et = (EditText) findViewById(R.id.messageField);
				et.setText("");

			}
		});
		 */
		Thread distanceThread = new Thread (new Runnable()
		{

			@Override
			public void run() 
			{
				String distanceString = getDistanceOnRoad(lastLocation.getLatitude(), lastLocation.getLongitude(), MainActivity.DEST_LAT, MainActivity.DEST_LNG);
				distanceString = "Distance: " + distanceString;
				toastOutputter.outputText(distanceString);
				textViewOutputter.outputText(distanceString);
				System.out.println(distanceString);
			}

		});
		distanceThread.start();
	}
	private void sendSMS(String phoneNumber, String message)
	{        
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
				new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		//---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
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
			public void onReceive(Context arg0, Intent arg1) {
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

		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(message); 
		sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

	}


}
