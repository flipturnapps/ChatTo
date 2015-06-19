package com.flipturnapps.android.test.chatto;

import android.content.Context;
import android.widget.TextView;

public class TextViewAreaComponent extends TextView {

	public TextViewAreaComponent(Context context, String text, int color) 
	{
		super(context);
		setUp(text,color);
	}

	private void setUp(String text, int color) 
	{
		this.setText(text);
		this.setTextColor(color);
		this.setTextSize(15);
		
	}

	

	

}
