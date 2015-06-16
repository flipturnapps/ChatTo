package com.flipturnapps.android.test.chatto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ChatToClient extends Socket implements Runnable
{


	private boolean alive = true;
	private Thread myThread;
	private TextOutputter outputter;
	private PrintWriter writer;
	private Activity activity;
	public ChatToClient(TextOutputter out, String ip, Activity a) throws IOException 
	{
		super(ip, ChatToServer.PORT);
		myThread = new Thread(this);
		myThread.start();
		this.outputter = out;
		this.activity = a;
	}

	@Override
	public void run()
	{
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) 
		{
			e1.printStackTrace();
		}
		this.outputter.outputText("Connection established");
		
		
		
		
		
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		while(alive)
		{
			String line = null;
			try 
			{
				line = reader.readLine();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			if(line != null && !(line.equals("")))
			{
				outputter.outputText(line);
			}
		}


	}



	

	public void close() throws IOException
	{
		super.close();
		alive = false;
		try
		{
			if(myThread != null)
				myThread.interrupt();
		}
		catch(Exception ex)
		{

		}
	}

	public void sendText(String text)
	{
		if(writer == null)
			try {
				writer = new PrintWriter(this.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		writer.println(text);
		writer.flush();
	}



}
