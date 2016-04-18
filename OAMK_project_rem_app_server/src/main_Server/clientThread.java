package main_Server;
import java.io.*;
import java.net.*;

import person.Person;
import person.medicineTimetables;
import person.notification;
/**
 *
 * @author juraj
 */
/* For every client's connection we call this class*/
public class clientThread extends Thread{

	private ServerSocket providerSocket = null;
	private Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	CommandsForServerCommunication commands = null;
	private Person person = null;

	
/**
 * Constructor 
 * @param clientSocket = client's socket
 * @param threads = the thread where is the communication between client and server 
 */
  public clientThread(ServerSocket ServerSocket, Socket connectionSocket)  throws java.net.SocketException{
    
	  	this.commands = new CommandsForServerCommunication();
	  	this.providerSocket = ServerSocket;
	  	this.connection = connectionSocket;
	  	this.run();
  }
/**
 * method for running the server connection using TCP protocol 
 */
 public void run(){
		
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
					//TODO call method with the commands 
					message = (String)in.readObject();
					System.out.println("massage from client >"+message);
					
					
						
						
						if(message.contains("-infoTimetables;")){
							for (medicineTimetables mtt : commands.user.returnArrayListOfTimetables()){
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
							sendMessage("-infoUserDone;"+commands.user.returnID()+";");
						}
						else
							sendMessage(commands.mainCommandsForCommunication(message));
						
					
					if(message.equals("Log Out"))
						sendMessage("Log Out");
					}catch(ClassNotFoundException classnot){
						System.err.println("Data received in unknown format");
					}
				}while(!message.equals("Log Out"));
		}catch( IOException ioException){
			ioException.printStackTrace();
		}
	finally{
		/*Closing the connection*/
		try{
			in.close();
			out.close();
			providerSocket.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		}
	}
 
 	private void fillPersonsData(){
 		//TODO add personal data of user to person object
 		//TODO add personal data of user's contact person to person object  
 		//TODO add personal data of timetables to person object 
  	}
 	
 	private String personalDataToSend(){
 		String messagePersonaldata="";
 		//TODO read data from person and make message 

 		return messagePersonaldata;
 	}
 	
 	
 	private String contactPersonDataToSend(){
 		String messageContactPersondata="";
 		//TODO read daata from person and make message

 		return messageContactPersondata;
 	}
 	
 	private String timetableDataToSend(){
 		String messageTimetableData="";
 		//TODO read data from person object and make message 

 		return messageTimetableData;
 	}
 	
   /**
	 *  method for sending the message 
	 */
	private void sendMessage(String msg){
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("Server sending >"+msg);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	/**
	 * This method is sending the message from the received from the client to server, server class is able to see it so server class can make changes in the database 
	 * @param s
	 * @return
	 */
	public String returnMessageToServer(String s){
		//TODO 
		return s;
	}
}