package com.flipturnapps.android.test.chatto;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable, LocationListener
{
	private String serverIp = null;
	private ChatToClient client;
	private TextOutputter outputter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		new Thread(this).start();
	}

	protected void onStop()
	{
		super.onStop();

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
		outputter = new ToastOutputter(this);
		if(serverIp == null)
		{
			try {
				Thread.sleep(1000);
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
			LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			manager.requestLocationUpdates(manager.GPS_PROVIDER, 10000, 0, this);
			manager.requestLocationUpdates(manager.NETWORK_PROVIDER, 10000, 0, this);
			useLocation(location);
		}
		else
		{
			try {
				client = new ChatToClient(this.outputter,serverIp, this);
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private void useLocation(Location location)
	{
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
		this.outputter.outputText(s);
		
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
		EditText editText = (EditText) findViewById(R.id.input);
		String text = editText.getText().toString();

		if(text.replace(".", "~").split("~").length==4)
		{
			serverIp = text;
			new Thread(getActivity()).start();
		}
		else
			client.sendText(text);
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{

				EditText area = (EditText) findViewById(R.id.input);
				area.setText("");

			}
		});
	}

}
