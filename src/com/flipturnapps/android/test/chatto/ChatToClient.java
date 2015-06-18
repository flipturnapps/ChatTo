package com.flipturnapps.android.test.chatto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatToClient extends Socket implements Runnable
{

	private static final String SERVERIP = "192.168.43.172";
	private static final int PORT = 12346;
	private static final long DEFAULT_WAIT_TIME = 3000;
	private boolean alive = true;
	private Thread myThread;
	private TextOutputter outputter;
	private PrintWriter writer;
	private String lastResponse;
	static ChatToClient self;
	public ChatToClient(TextOutputter out) throws IOException 
	{
		super(SERVERIP, PORT);
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
		self = this;



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
				readLine(line);
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
			} catch (IOException e) 
		{				

				e.printStackTrace();
		}
		writer.println(text);
		writer.flush();
	}
	public String aquireNextResponse(String command) 
	{
		return this.aquireNextResponse(command, DEFAULT_WAIT_TIME);
	}
	public String aquireNextResponse(String command, long waitTime) 
	{
		lastResponse = null;
		this.sendText(command);
		long startTime = System.currentTimeMillis();
		while(lastResponse == null && System.currentTimeMillis() - startTime < waitTime)
		{
			try 
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}			
		}
		return lastResponse;
	}
	public void readLine(String line)
	{
		//outputter.outputText(line);
		lastResponse = line;
	}



}
