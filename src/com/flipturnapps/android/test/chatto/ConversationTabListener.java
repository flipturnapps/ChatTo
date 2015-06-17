package com.flipturnapps.android.test.chatto;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;

public class ConversationTabListener implements ActionBar.TabListener
{

	private Fragment fragment;
	private Context context;
	private String tag;

	public ConversationTabListener(Context context, String tag)
	{
		this.context = context;
		this.tag = tag;
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		if (fragment == null) 
		{
			fragment = Fragment.instantiate(context, ConversationFragment.class.getName());
			ft.add(android.R.id.content, fragment, tag);
		} else 
		{
			ft.attach(fragment);
		}
	}


	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
		if (fragment != null) 
		{
			ft.detach(fragment);
		}
	}



	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) 
	{

	}


}
