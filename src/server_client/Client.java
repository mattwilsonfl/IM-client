package server_client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame
{
	private JTextField userText;
	private JTextArea chatWindow;
	String message = "";
	String serverIP;
	ServerSocket ss1;
	Socket socket1;
	ObjectInputStream input;
	ObjectOutputStream output; 
	
	public Client(String host)
	{
		super("Matt's client! ");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener
		(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent event)
				{
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}		
		);
		
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}
	
	public void startRunning()
	{
		try
		{
			connectToServer();
			setUpStreams();
			whileChatting();
		}
		catch (EOFException eOFException)
		{
			showMessage("\n Client terminated exception!");
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
		finally
		{
			closeCrap();
		}
	}
	
	private void connectToServer() throws IOException
	{
		showMessage("Attempting to connect...\n");
		socket1 = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + socket1.getInetAddress().getHostName());
	
	}
	
	private void setUpStreams() throws IOException
	{
		output = new ObjectOutputStream(socket1.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket1.getInputStream());
		showMessage("Your streams are good-to-go! \n");
	}
	
	private void whileChatting() throws IOException
	{
		ableToType(true);
		do
		{
			try
			{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}
			catch (ClassNotFoundException classNotFoundException)
			{
				showMessage("\n I don't know that object type..");
			}
		} while (!message.equals("SERVER - END"));
	}
	
	private void closeCrap()
	{
		showMessage("\n closing program");
		ableToType(false);
		try
		{
			output.close();
			input.close();
			socket1.close();
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage(String message)
	{
		try
		{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}
		catch (IOException ioException)
		{
			chatWindow.append("\n something messed up sending messages.");
		}
	}
	
	private void showMessage(final String m)
	{
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{
						chatWindow.append(m);
					}
				}
	    );
	}
	
	private void ableToType(final boolean tof)
	{
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{
						userText.setEditable(tof);;
					}
				}
	    );
	}
}
