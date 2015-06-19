package com.flipturnapps.android.test.chatto;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class TextViewArea extends LinearLayout 
{

	public TextViewArea(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TextViewArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TextViewArea(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	public TextViewAreaComponent addLine(String text, int color)
	{
		this.setLayoutDirection(LinearLayout.VERTICAL);		
		TextViewAreaComponent view = new TextViewAreaComponent(this.getContext(),text,color);
		this.addView(view);		
		return view;
	}
	
	
	

}
