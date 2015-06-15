package com.flipturnapps.android.test.chatto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
			this.accept();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		this.outputter.outputText("Client connected!");
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
			while(alive)
			{
				String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
		myThread.interrupt();
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
