package com.flipturnapps.android.test.chatto;

import android.app.Activity;

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

				TextViewArea area = (TextViewArea) activity.findViewById(R.id.textViewArea_messageArea);
				area.addLine(s, area.getResources().getColor(R.color.text_color_recieve));

			}
		});		

	}

}


