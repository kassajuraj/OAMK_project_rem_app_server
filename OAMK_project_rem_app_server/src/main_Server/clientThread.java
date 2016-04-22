package main_Server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
    private String clientName;
    private final clientThread[] threads;
    private int maxClientsCount;
    private ArrayList<Person> toCallPersons;
    private boolean usersDataLoaded = false;
 /**
  * Constructor 
  * @param clientSocket = client's socket
  * @param threads = the thread where is the communication between client and server 
  */
  public clientThread(Socket clientSocket, clientThread[] threads, int i, ArrayList<Person> toCallPersons) {
		    this.connection = clientSocket;
		    this.threads = threads;
		    maxClientsCount = threads.length;
		    this.clientName = "client_"+i;
		    this.commands = new CommandsForServerCommunication();
		    this.toCallPersons = toCallPersons;
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
		                	  else if(message.equals("Log Out"))
		                		  sendMessage("Log Out");
		                	  else
		                		  sendMessage(commands.mainCommandsForCommunication(message));
		                	  
		                	  if(usersDataLoaded){
		                		  Person person = threads[i].returnCommandsForCommunication().returnUser();
		    		        	  for(Person p : toCallPersons){
		    		        		 // System.out.println(p.returnName());
		    		        		  if(p.returnCallMe().equals("toCall")){
		    		        			  if(person.returnName().equals(p.returnName()) && person.returnSurname().equals(p.returnSurname()) && person.returnTelNumber().equals(p.returnTelNumber())){
		    		        				  /*send message to user to control his/her contact person*/
		    		        				  System.out.println("Sending message to contact person "+p.returnName());
		    		        				  threads[i].sendMessage("controlCP");
		    		        				  /*set call status to waiting until will receive answer from contact person*/
		    		        				  p.setCallMe("waiting");
		    		        				  
		    		        				  if(threads[i].receiveMessage().equals("controled"))
		    		        					  p.setCallMe("Ok");
		    		        			  }
		    		        		  }
		    		        		  else if(p.returnCallMe().equals("Ok"))
		    		        			  toCallPersons.remove(p);
		    		        			  
		    		        	  }
		    		          }
		                	  
		                		  	  
		               }
					 }
					}
		        }catch(ClassNotFoundException classnot){
		                	  
					 }
				}while(!message.equals("Log Out"));
			synchronized(this){
				  for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] != null && threads[i] != this
			              && threads[i].clientName != null) {
			            System.out.println(clientName+" has been logged out...");
			          }
			        }
			}
			
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
		} catch (ClassNotFoundException e) {
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
			out.writeObject(msg);
			out.flush();
			System.out.println("Server sending >"+msg);
		}catch(IOException ioException){
			ioException.printStackTrace();
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