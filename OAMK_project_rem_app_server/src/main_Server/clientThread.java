package main_Server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

import person.Person;
import person.medicineTimetables;
import person.notification;
/**
 *
 * @author juraj
 */
/* For every client's connection we call this class*/
public class clientThread extends Thread{

	private Socket connection = null;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message;
	private CommandsForServerCommunication commands = null;
    public String clientName;
    private final clientThread[] threads;
    private int maxClientsCount;
    
    private boolean usersDataLoaded = false;
    private boolean test = false;
 /**
  * Constructor 
  * @param clientSocket = client's socket
  * @param threads = the thread where is the communication between client and server 
  */
  public clientThread(Socket clientSocket, clientThread[] threads, int i) {
		    this.connection = clientSocket;
		    this.threads = threads;
		    maxClientsCount = threads.length;
		    this.clientName = "client_"+i;
		    this.commands = new CommandsForServerCommunication();
		  }

/**
 * method for running the server connection using TCP protocol 
 */
 public void run(){
	
	 int maxClientsCount = this.maxClientsCount;
	 clientThread[] threads = this.threads;
	 
	try{
		/*wait for connection*/
		System.out.println("Connection has been added");
		System.out.println("Connection successful");

		System.out.println("Connection received from "+ connection.getInetAddress().getHostName());		
			/*get Input and Output streams */
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			sendMessage("1.Welcome");
			//controlNotifications();
			/*The two parts communicate via the input and output stream*/
			do{
				try{
					
					synchronized(this){
					 for (int i = 0; i < maxClientsCount; i++) {
		                  if (threads[i] != null && threads[i].clientName != null && threads[i].clientName.equals("client_"+i)) {
		                	  message = (String)in.readObject();
		                	  System.out.println("massage from client_"+i+" >"+message);
						
		                	  if(message.contains("-infoTimetables;") || message.contains("+check update;")){
		                		 if(message.contains("-infoTimetables;"))
		                			 this.sendTimetables("load");
		                		 else
		                			 this.sendTimetables("update");
		                	  }  
		                	  else if(message.equals("Log Out")){
		                		  sendMessage("Log Out");
		                		  test = false;
		                	  }
		                	  else
		                		  sendMessage(commands.mainCommandsForCommunication(message));
		                	  
		                	  //TODO make new message for update notifications 
		                	  //TODO make only local list of persons and read data from here and save data to this list too 
		                	// if(!message.equals("Log Out"))
		                	//  if(usersDataLoaded)
		                	//	  test=true;
		                	  
		                	//  if(test)
		                		  
		                	  
		                		  	  
		               }
					 }
					}
		        }catch(ClassNotFoundException | java.io.StreamCorruptedException classnot){
		                	  
					 }
				}while(!message.equals("Log Out"));
	/*		synchronized(this){
				  for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] != null && threads[i] != this
			              && threads[i].clientName != null) {
			            System.out.println(clientName+" has been logged out...");
			          }
			        }
			}
	*/		
			synchronized (this) {
		        for (int i = 0; i < maxClientsCount; i++) {
		          if (threads[i] == this) {
		            threads[i] = null;
		          }
		        }
		      }
			
			
			
		}catch( IOException ioException){
			ioException.printStackTrace();
		}
	finally{
		/*Closing the connection*/
		try{
			
			in.close();
			out.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		}
	}
 
 	public String receiveMessage(){
 		try {
 			String msg = (String)in.readObject();
 			System.out.println("Server received < "+msg);
			return msg;
		} catch (ClassNotFoundException | java.io.StreamCorruptedException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return null;
 	}
 
 
   /**
	 *  method for sending the message 
	 */
	public void sendMessage(String msg){
		try{
			if(connection.isConnected()){
			out.writeObject(msg);
			out.flush();
			System.out.println("Server sending >"+msg);
			}
		}catch(IOException ioException){
		//	ioException.printStackTrace();
		}
	}
	/**
	 * method for sending information about timetables when the user sign in
	 */
	private void sendTimetables(String s){
		 for (medicineTimetables mtt : commands.returnUser().returnArrayListOfTimetables()){
			  /*message form "-infoTimetables;IDOftimetable;dateFrom;timeFrom;dateUntil;timeUntil;active;"*/
			  message = "-infoTimetables;"+mtt.returnIdOfTimetable()+";"+mtt.returnDate("from")+";"+mtt.returnTime("from")+";"+mtt.returnDate("to")+";"+mtt.returnTime("to")+";"+mtt.returnActive()+";";
			  sendMessage(message);
			  /*message form "-infoMedicine;IDOftimetable;IdOfmedicine;nemaOfMedicine;StrengthOfMedicine;dayLimitOfMedicine;daysLimitOfMedicine"*/
			  String medicineMessage = "-infoMedicine;"+mtt.returnIdOfTimetable()+";"+mtt.returnMedicine().returnIDOfMedicine()+";"+mtt.returnMedicine().returnNameOfMedicine()+";"+mtt.returnMedicine().returnStrengthOfMedicine()+";"+mtt.returnMedicine().returnDayLimit()+";"+mtt.returnMedicine().returnDaysLimit()+";";
			  sendMessage(medicineMessage);
			  for(notification not : mtt.returnArrayListOfNotificaations()){
				  /*message form "-infoNotifications;IdOftimetable;notificationDate;notificationTime;notificationStatus;"*/
				  String notificationMessage = "-infoNotifications;"+mtt.returnIdOfTimetable()+";"+not.returnNotificationDate()+";"+not.returnNotificationTime()+";"+not.returnNotificationStatus()+";";
				  sendMessage(notificationMessage);
			  }
		  }
		 if(s.equals("update"))
			 sendMessage("+timetablesuptodate;");
		 else
		  sendMessage("-infoUserDone;"+commands.returnUser().returnID()+";");
		 usersDataLoaded = true;
	}
	
	public CommandsForServerCommunication returnCommandsForCommunication(){
		return this.commands;
	}
		
	
	
}