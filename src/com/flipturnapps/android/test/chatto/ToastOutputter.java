package com.flipturnapps.android.test.chatto;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Toast;

public class ToastOutputter implements TextOutputter {

	private Activity activity;
	public ToastOutputter(MainActivity mainActivity) 
	{
		activity = mainActivity;
	}



	@Override
	public void outputText(final String s) 
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{

				Toast toast = Toast.makeText(activity, s, Toast.LENGTH_SHORT);
				toast.show();
			}
		});		

	}

}


