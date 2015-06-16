package com.flipturnapps.android.test.chatto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatToClient extends Socket implements Runnable
{


	private boolean alive = true;
	private Thread myThread;
	private TextOutputter outputter;
	private PrintWriter writer;
	public ChatToClient(TextOutputter out, String ip) throws IOException 
	{
		super(ip, ChatToServer.PORT);
		myThread = new Thread(this);
		myThread.start();
		this.outputter = out;
	}

	@Override
	public void run()
	{
		try 
		{
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
