package main_Server;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;
import ConsolePackage.TextAreaOutputStream;

/**
*
* @author juraj
*/

public class server {
	
	ServerSocket providerSocket;
	ArrayList<clientThread> connectionThreadList = new ArrayList<clientThread>();
	String message;
	
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
	
	
	/**
	 * Start the server and make arraylist of connection everytime when new connection start it like the new clientThread 
	 */
	void runServer(){
		
		//TODO open database 
		
	try{
		/*creating a server socket */
		providerSocket = new ServerSocket(2004, 10);
		/*wait for connection*/
		System.out.println("Server is running...");
		System.out.println("Waiting for connection...");
		
		//TODO make database like parameter for every threadConnection with client 
		connectionThreadList.add(new clientThread(providerSocket, providerSocket.accept()));
		
		//TODO endless loop and in this loop will be the loop for control all clientThreads 
		//TODO make loop which will control all clientThreads if will some disconnect remove it
		
		
		}catch( IOException ioException){
			ioException.printStackTrace();
		}
	}
		
}
