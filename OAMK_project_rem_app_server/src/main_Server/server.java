package main_Server;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import ConsolePackage.TextAreaOutputStream;
import person.Person;
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
	final int maxClientsCount = 10;
	final clientThread[] threads = new clientThread[maxClientsCount];

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
	 * method for monitoring notifications saved in database 
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
		
		 int portNumber = 2004;
		 providerSocket = new ServerSocket(portNumber);
		/*wait for connection*/
		System.out.println("Server is running...");
		System.out.println("Waiting for new connections...");
		this.controlNotifications();
		
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
	
	/**
	 * 
	 */
	private void disconnectServer(){
		System.out.println("Closing socket....");
		try {
			
			for (int i = 0; i < maxClientsCount; i++){ 
		        if(threads[i].isAlive()){  
		        	threads[i].destroy();
		          	threads[i] = null;
		        }
			 }
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
	/**
	 *method forr control notifications and sending message to contact person  
	 */
	public void controlNotifications(){
		/*  New thread with endless loop which will control actual time and compare with arrayList of notifications id database*/
		Thread monitorNotificationThread = new Thread(){
			
			public void run(){
				ArrayList<Person> toCallPersons = new ArrayList <Person>();
				int countOfLoops = 0;
				while(true){
					
					ArrayList<Person> arp = null;
								if(countOfLoops==15){  // Every 15 loops remove all persons in loop which have "CallMe" status "waiting"
									countOfLoops =0;
									for(Person prsn : toCallPersons)
										if(prsn.returnCallMe().equals("waiting"))
											toCallPersons.remove(prsn);
								}else{
									if(toCallPersons.isEmpty())
										System.out.println("persons list is empty");
									else{
									System.out.println("Minute "+countOfLoops+" person list contains");
									for(Person prsn : toCallPersons)
										System.out.println(prsn.returnName()+" "+prsn.returnSurname()+" "+prsn.returnCallMe());
									System.out.println("------------------------");
									}
								}
					/* Every loop increment counter of loops and set found to false*/			
					boolean found = false;
					countOfLoops++;
					try{	
						for (int i = 0; i < maxClientsCount; i++) {
			                  if (threads[i] != null && threads[i].clientName != null && threads[i].clientName.equals("client_"+i)){
			                	  if(arp == null)
			                		  arp = threads[i].returnCommandsForCommunication().returnDatabase().toCallContactPersonList();
			                	  /*Add persons from "arp" list recorded from database to "toCallPersons" list if the person is not in this list yet*/
			                	  for(Person per : arp){
			                		  if(toCallPersons.isEmpty())
		                			  		toCallPersons.add(per); //if empty list add person
			                		  else{
			                			  for(Person user : toCallPersons){ // if not empty then compare person from "arp" list and "toCallPersons" 
			                				  if(user.returnName().equals(per.returnName()) && user.returnSurname().equals(per.returnSurname()) && user.returnTelNumber().equals(per.returnTelNumber())){
			                					  found = true;
			                					  break;
			                				  }
			                			  	}
			                		  		if(found == false)// if not found the person from "arp" list in "toCallPerons" list then add person 
			                		  			toCallPersons.add(per); 
			                		  	}
			                	  }
			                	  /*Find person in list and connection with server if connection exists then send message and change "CallMe" status in person*/
			                	  Person person = threads[i].returnCommandsForCommunication().returnUser();
			                	  for(Person p : toCallPersons){
				  		        		  if(p.returnCallMe().equals("toCall")){
				  		        			  if(person != null)
				  		        				  if(person.returnName().equals(p.returnName()) && person.returnSurname().equals(p.returnSurname()) && person.returnTelNumber().equals(p.returnTelNumber())){
				  		        					  /*send message to user to control his/her contact person*/
				  		        						threads[i].sendMessage("controlCP");
				  		        					  p.setCallMe("waiting");
				  		        			  }
				  		        		  }
				  		        	  }
			                  }
  		        	  }
					}catch(java.lang.NullPointerException | java.util.ConcurrentModificationException e){
						
					}
				try {
					Thread.sleep(60*1000);	// after done then sleep for one mminute 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				}
			}
		};
		monitorNotificationThread.start();
	}
}
