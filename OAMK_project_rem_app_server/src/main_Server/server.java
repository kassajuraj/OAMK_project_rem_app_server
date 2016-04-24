package main_Server;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Date;
import javax.swing.*;
import ConsolePackage.TextAreaOutputStream;
/**
*
* @author juraj
*/

public class server implements ActionListener{
	
	private ServerSocket providerSocket;
	private database db = new database();
	Boolean runningServer = false;
	JButton disconect = new JButton("Disconnect");
	JButton reset = new JButton("Reset server");
	Socket clientSocket = null;
	/**
	 * Constructor 
	 */
	server(){
		runningServer =true;
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
		panel.add(disconect);
		panel.add(reset);
		 disconect.addActionListener(this);
		 reset.addActionListener(this);
		
		return panel;	
	}
	

	
	
	/**
	 * method forr monitoring notifications saved in database 
	 */
	void MonitoringNotifications(){
		/* New thread with endless loop which will control actual time and compare with arrayList of notifications id database*/
		Thread monitorNotificationThread = new Thread(){
			
			public void run(){
				while(runningServer){
					System.out.println("Updating the notification database...");
				OurDateClass now = new OurDateClass(new Date());
				db.controlNotifications(now.returnDate(), now.returnTime());				
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
			}
		};
		monitorNotificationThread.start();
		
	}
	
	/**
	 * Start the server and make arraylist of connection everytime when new connection start it like the new clientThread 
	 */
	void runServer(){

	try{
		final int maxClientsCount = 10;
		final clientThread[] threads = new clientThread[maxClientsCount];
		 int portNumber = 2004;
		 providerSocket = new ServerSocket(portNumber);
		/*wait for connection*/
		System.out.println("Server is running...");
		System.out.println("Waiting for new connections...");
		
		
		 while (runningServer) {
		        clientSocket = providerSocket.accept();
		        int i = 0;
		        for (i = 0; i < maxClientsCount; i++) {
		          if (threads[i] == null) {
		            (threads[i] = new clientThread(clientSocket, threads, i)).start();
		            break;
		          }
		        }
		        if (i == maxClientsCount) {
		          ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
		          os.writeObject("Server too busy. Try later.");
		          os.close();
		          clientSocket.close();
		        }
		    }

		}catch( IOException ioException){
			
		}
	}
	
	
	private void disconnectServer(){
		System.out.println("Closing socket....");
		try {
			runningServer = false;
			providerSocket.close();
			if(providerSocket.isClosed())
				System.out.println("Server connection has been close");
			
		} catch (IOException e) {

		}
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(disconect ==(JButton)arg0.getSource())
			this.disconnectServer();
		else if(reset == (JButton)arg0.getSource()){
			System.out.println("Reseting server....");
			this.disconnectServer();
			this.runServer();
			this.MonitoringNotifications();
		}
		
	}
	
}
