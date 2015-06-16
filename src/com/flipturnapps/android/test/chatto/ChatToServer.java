package com.flipturnapps.android.test.chatto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.android.gms.maps.GoogleMap;

public class ChatToServer extends ServerSocket implements Runnable
{
	public static final int PORT = 12346;
	private Socket client;
	private boolean alive = true;
	private Thread myThread;
	private ChatTextOutputter outputter;
	private PrintWriter writer;
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
			Thread.sleep(2000);
		} catch (InterruptedException e1) 
		{
			e1.printStackTrace();
		}
			this.outputter.outputText("Ip to connect: " + this.getInetAddress().toString());
		
		try {
			this.accept();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		if(client != null)
		this.outputter.outputText("Client connected: " + client.getInetAddress().toString());
		else
			this.outputter.outputText("Client is null");
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
			while(alive)
			{
				String line = null;
				try {
					line = reader.readLine();
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
				if(line != null && !(line.equals("")))
				{
					outputter.outputText(line);
				}
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
		try
		{
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
				writer = new PrintWriter(client.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		writer.println(text);
		writer.flush();
	}
	

}
