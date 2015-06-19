package com.flipturnapps.android.test.chatto;

import android.app.Activity;

public class TextViewOutputter 
{

	private Activity activity;
	public TextViewOutputter(MainActivity mainActivity) 
	{
		activity = mainActivity;
	}



	
	public void outputText(final String s, final int colorId) 
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{

				TextViewArea area = (TextViewArea) activity.findViewById(R.id.textViewArea_messageArea);
				area.addLine(s, area.getResources().getColor(colorId));

			}
		});		

	}

}


