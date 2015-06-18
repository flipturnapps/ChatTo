package com.flipturnapps.android.test.chatto;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements Runnable, LocationListener
{


	private TextOutputter toastOutputter;
	private Thread thread;
	private Location lastLocation;
	private double latitude;
	private double longitude;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
	@Override
	public void run()
	{
		toastOutputter = new ToastOutputter(this);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
/*
		Looper.prepare();
		output("hmm?");
		
		LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);

	    String provider = locationManager.getBestProvider(criteria, true);
	    if(provider == null){
	        provider = LocationManager.GPS_PROVIDER;
	    }
	    locationManager.requestLocationUpdates(provider, 1000, 0, this);
	
		while(this.lastLocation == null)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		*/
		latitude = 44.5672590;
		longitude = -123.2778470;
			this.runOnUiThread(new Runnable()
			{
				

				@Override
				public void run()
				{
					GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
					MarkerOptions mOptions = new MarkerOptions();
					mOptions.position(new LatLng(latitude,longitude));
					mOptions.title("Your location");
					mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_for_map));
					Marker locationMarker = map.addMarker(mOptions);
					output("marker shown.");
				}

			});
		
		output("done");


	}
	/* CURRENTLY DISABLED
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
	 */
	private void useLocation(Location location)
	{

		this.lastLocation = location;
		output("Location Updated!");

	}

	@Override
	public void onLocationChanged(Location l)
	{
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


	void output(String s)
	{
		System.out.println("Toasted: " + s);
		toastOutputter.outputText(s);
	}


}
