package com.flipturnapps.android.test.chatto;

import android.app.Activity;
import android.widget.EditText;

public class TextViewOutputter implements TextOutputter {

	private Activity activity;
	public TextViewOutputter(MainActivity mainActivity) 
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

				EditText area = (EditText) activity.findViewById(R.id.textView1);
				String text = area.getText().toString();
				text += "\n"  + s;
				area.setText(text);

			}
		});		

	}

}


