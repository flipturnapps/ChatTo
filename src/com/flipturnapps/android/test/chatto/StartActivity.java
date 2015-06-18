package com.flipturnapps.android.test.chatto;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends Activity 
{

	private static final int PICK_CONTACT = 1;
	static final String CONTACT_NAME_EXTRA = "com.flipturnapps.android.text.chatto.EXTRA.CONTACT_NAME_EXTRA";
	static final String CONTACT_PHONENUM_EXTRA = "com.flipturnapps.android.text.chatto.EXTRA.CONTACT_PHONENUM_EXTRA";
	private Thread thread;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		if(thread == null)
		{
		thread = new Thread(new StartThreadRunner());
		thread.start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{
		super.onActivityResult(reqCode, resultCode, data);
		if (reqCode == PICK_CONTACT && resultCode == Activity.RESULT_OK)
		{

			Uri contactData = data.getData();
			Cursor c =  managedQuery(contactData, null, null, null, null);
			if (c.moveToFirst()) 
			{


				String id =c.getString(c.getColumnIndexOrThrow(BaseColumns._ID));

				String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

				if (hasPhone.equalsIgnoreCase("1"))
				{
					Cursor phones = getContentResolver().query( 
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, 
							null, null);
					phones.moveToFirst();

					String cNumber = phones.getString(phones.getColumnIndex("data1"));
					setPhoneNumberText(cNumber);
				}
				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				setContactNameText(name);
			}
		}

	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
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
			View rootView = inflater.inflate(R.layout.fragment_start,
					container, false);
			return rootView;
		}
	}


	private class StartThreadRunner implements Runnable
	{
		public void run()
		{
			try 
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			registerButtonListeners();			
		}
	}	
	private void registerButtonListeners()
	{
		this.runOnUiThread(new ButtonListenerRegisterer());
	}
	private class ButtonListenerRegisterer implements Runnable
	{
		public void run() 
		{
			Button ccButton = (Button) findViewById(R.id.button_chooseContact);
			ccButton.setOnClickListener(new ChooseContactButtonListener());
			Button goButton = (Button) findViewById(R.id.button_go);
			goButton.setOnClickListener(new StartButtonListener());
		}		
	}
	private class ChooseContactButtonListener implements View.OnClickListener
	{		
		public void onClick(View v) 
		{
			onChooseContactButtonClicked();			
		}		
	}
	private class StartButtonListener implements View.OnClickListener
	{		
		public void onClick(View v) 
		{
			onStartButtonClicked();		
		}		
	}
	private void onStartButtonClicked()
	{
		String contactName = getContactNameText();
		String phoneNum = getPhoneNumText();
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(StartActivity.CONTACT_NAME_EXTRA, contactName);
		intent.putExtra(StartActivity.CONTACT_PHONENUM_EXTRA, phoneNum);
		this.startActivity(intent);
	}
	private String getPhoneNumText() 
	{
		EditText field = (EditText) findViewById(R.id.editText_contactPhonenum);
		return field.getText().toString();
	}

	private String getContactNameText() 
	{
		EditText field = (EditText) findViewById(R.id.editText_contactName);
		return field.getText().toString();
	}

	private void onChooseContactButtonClicked()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);
	}
	private void output(String string) 
	{
		System.out.println("Toasted:" + string);
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}
	private void setContactNameText(String name)
	{
		this.runOnUiThread(new EditTextSetter(R.id.editText_contactName,name));
	}

	private void setPhoneNumberText(String number)
	{
		this.runOnUiThread(new EditTextSetter(R.id.editText_contactPhonenum,number));		
	}
	
	private class EditTextSetter implements Runnable
	{
		private int id;
		private String text;

		public EditTextSetter(int id, String t)
		{
			this.id = id;
			this.text = t;
		}

		@Override
		public void run()
		{
			EditText field = (EditText) findViewById(id);
			field.setText(text);
		}
	}
}
