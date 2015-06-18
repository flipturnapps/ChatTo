package com.flipturnapps.android.test.chatto;

import java.io.InputStream;
import java.util.ArrayList;

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
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
	private BasicTextEncryptor encryptor;
	private Location lastLocation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
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
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}

		this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, getActivity());
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, getActivity());
				useLocation(location);
			}

		});

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		GoogleMap map = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.map)).getMap();
		MarkerOptions mOptions = new MarkerOptions();
		mOptions.position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
		mOptions.title("Your location");
		mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_for_map));
		Marker locationMarker = map.addMarker(mOptions);

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
