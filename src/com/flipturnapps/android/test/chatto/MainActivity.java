package com.flipturnapps.android.test.chatto;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements  LocationListener
{



	private boolean marker;
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Thread(new UIThreadRunner("delay")).start();


	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	MainActivity getActivity()
	{
		return this;
	}

	private class UIThreadRunner implements Runnable
	{
		private String text;
		

		public UIThreadRunner(String toastText)
		{
			text = toastText;
		}

		@Override
		public void run()
		{
			System.out.println("hello?");
			if(text != null)
			{
				if(text.equals("delay"))
				{
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runOnUiThread(new UIThreadRunner(null));
					return;
				}
				Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
				return;				
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, getActivity());
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, getActivity());
			toast("added location",false);
			Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(l == null)
				locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(l != null)
			useLocation(l);
			else
				toast("l is null",false);
		}
	}
	private void useLocation(final Location location)
	{
		if(!marker)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{
					GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
					MarkerOptions mOptions = new MarkerOptions();
					mOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
					mOptions.title("Your location");
					mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_for_map));
					Marker locationMarker = map.addMarker(mOptions);
					marker = true;
					toast("Marker added",false);	
					//locationManager.removeUpdates(getActivity());
				}

			});

		}
		else
			toast("Marker already added",true);
	}

	@Override
	public void onLocationChanged(Location l)
	{
		toast("onlicationchange",true);
		useLocation(l);
	}

	@Override
	public void onProviderDisabled(String arg0) {


	}

	@Override
	public void onProviderEnabled(String arg0) {


	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {


	}


	void toast(String s, boolean needUI)
	{
		System.out.println("Toasted: " + s);
		if(!needUI)
		{
			Toast.makeText(this, s, Toast.LENGTH_LONG).show();
			return;
		}
		this.runOnUiThread(new UIThreadRunner(s));
	}


}
