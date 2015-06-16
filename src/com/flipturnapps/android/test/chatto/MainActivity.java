package com.flipturnapps.android.test.chatto;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
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

public class MainActivity extends Activity implements ChatTextOutputter
{
	private ChatToServer server;
	private boolean init;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		try {
			server = new ChatToServer(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void onStop()
	{
		super.onStop();
		try {
			server.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
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

	@Override
	public void outputText(final String s) 
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{

				EditText area = (EditText) findViewById(R.id.chatspace);
				String text = area.getText().toString();
				text += "\n"  + s;
				area.setText(text);

			}
		});
		if(!init)
		{
			final EditText.OnEditorActionListener listener = new EditText.OnEditorActionListener()
			{

				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
					if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) 
					{ 

						server.sendText(v.getText().toString());
					}
					return true;
				}

			};
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{

					EditText area = (EditText) findViewById(R.id.chatspace);
					area.setOnEditorActionListener(listener);

				}
			});
			init = true;

		}

	}
}
