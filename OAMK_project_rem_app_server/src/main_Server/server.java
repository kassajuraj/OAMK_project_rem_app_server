package main_Server;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import ConsolePackage.TextAreaOutputStream;

/**
*
* @author juraj
*/

public class server {
	
	private ServerSocket providerSocket;
	private String message;
	private database db = new database();
	ArrayList<Integer> IdsOfUserToBeNotify = new ArrayList<Integer>();
	/**
	 * Constructor 
	 */
	server(){
		
	}
	
	/**
	 *  method which makes GUI for the server 
	 */
	public void makeEasyGuiForServer(){
	
		JFrame frameServer = new JFrame("Server");
		frameServer.setLayout(new BorderLayout());
		
		JTextArea ta = new JTextArea();
        TextAreaOutputStream taos = new TextAreaOutputStream( ta, 160 );
        PrintStream ps = new PrintStream(taos);
        System.setOut( ps );
        System.setErr( ps );
        
        frameServer.pack();
        
        frameServer.add(new JLabel("Output from server"), BorderLayout.PAGE_START);
        frameServer.add(new JScrollPane(ta), BorderLayout.CENTER);
		frameServer.add(rightPanel(), BorderLayout.LINE_END);
		frameServer.add(new JLabel("OAMK, Remme app"), BorderLayout.PAGE_END);
		
		frameServer.setSize(800, 500);
		
		frameServer.setVisible(true);
		frameServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * panel for buttons 
	 * @return
	 */
	private JPanel rightPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JButton disconect = new JButton("Disconnect");
		JButton reset = new JButton("Reset server");
		
		panel.add(disconect);
		panel.add(reset);
		//TODO Buttons actionListeners
		//TODO "disconect" buttons loop for arraylist and close all connections in this loop then close server 
		//TODO "reset: button loop for arraylist and close all connections in this loop then close server and then make the new runServer method.
		
		return panel;	
	}
	
	
	void MonitoringNotifications(){
		//TODO  New thread with endless loop which will control actual time and compare with arrayList of notifications id database
		Thread monitorNotificationThread = new Thread(){
			public void run(){
				OurDateClass now = new OurDateClass(new Date());
				IdsOfUserToBeNotify = db.controlNotifications(now.returnDate(), now.returnTime());
				
			}
		};
		monitorNotificationThread.start();
		
	}
	
	/**
	 * Start the server and make arraylist of connection everytime when new connection start it like the new clientThread 
	 */
	void runServer(){
		
		//TODO open database 
		
	try{
		/*creating a server socket */
		Socket clientSocket = null;
		final int maxClientsCount = 10;
		final clientThread[] threads = new clientThread[maxClientsCount];
		 int portNumber = 2004;
		 providerSocket = new ServerSocket(portNumber);
		
		/*wait for connection*/
		System.out.println("Server is running...");
		System.out.println("Waiting for connection...");
		
		
		 while (true) {
		      try {
		        clientSocket = providerSocket.accept();
		        int i = 0;
		        for (i = 0; i < maxClientsCount; i++) {
		          if (threads[i] == null) {
		            (threads[i] = new clientThread(clientSocket, threads, i)).start();
		            break;
		          }
		          //threads[i].returnCommandsForCommunication().returnUser().returnID();
		        }
		        if (i == maxClientsCount) {
		          PrintStream os = new PrintStream(clientSocket.getOutputStream());
		          os.println("Server too busy. Try later.");
		          os.close();
		          clientSocket.close();
		        }
		      } catch (IOException e) {
		        System.out.println(e);
		      }
		    }

		}catch( IOException ioException){
			ioException.printStackTrace();
		}
	}
		
}
