package com.flipturnapps.android.test.chatto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatToServer extends ServerSocket implements Runnable
{
	public static final int PORT = 12346;
	private Socket client;
	private boolean alive = true;
	private Thread myThread;
	private ChatTextOutputter outputter;
	public ChatToServer(ChatTextOutputter out) throws IOException 
	{
		super(PORT);
		myThread = new Thread(this);
		myThread.start();
		this.outputter = out;
	}
	
	@Override
	public void run()
	{
		try {
			this.accept();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			while(alive)
			{
				String line = reader.readLine();
				if(line != null && !(line.equals("")))
				{
					outputter.outputText(line);
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public Socket accept() throws IOException
	{
		Socket s = super.accept();
		client = s;
		return s;
	}
	
	public void close() throws IOException
	{
		super.close();
		alive = false;
		myThread.interrupt();
	}
	

}
