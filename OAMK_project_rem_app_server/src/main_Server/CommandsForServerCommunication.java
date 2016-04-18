package main_Server;
import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.*;

import person.Person;

/**
 * 
 * @author juraj
 *
 */
public class CommandsForServerCommunication {
	
	/*make database class which is connection to external database*/
	database db = new database();
	public Person user = null;
	/**
	 * Constructor
	 */
	CommandsForServerCommunication(){
		
		
	}
	/**
	 * This will be the only one method which will be calling in the clientThread communication, 
	 * inside this method will be the switch to control if is this command for some method inside this class or it is incorrect command 
	 * @param s it is the parameter which will be receive from the client
	 * @return the command for the clientThread class and then this command will be return to server class
	 */
	public String mainCommandsForCommunication(String s){
		//TODO switch for the calling the right method inside this class it will read only the first character from command string
		/*
		 * '~' = sign in 
		 * '!' = make notification
		 * '$' = register to the system
		 * '#' = edit profile  
		 * 	'%' = search medicine
		 */
		switch(s.charAt(0)){
		
		case '~' : s = this.commandsForSignIn(s); break;
		case '!' : 		this.notificationMethod(s); break;
		case '$' : s = this.commandsForRegistration(s); break;
		case '#' : s = this.commandForEditProfile(s); break; 
		case '-' : s = this.getpersonalDataFromDB(s); break;
		case '*' : s = this.workWithTimetable(s); break;
		case '%' : s = this.LookForNameOfMedicineInDatabase(showStringNumber(s, 1)); break;
		default : //TODO invalid action
			//TODO make message for edit my own profile where I can change my mail, password, name, surname, tel. number and my contact person not neccesary it is only updating the info
		}
		
		return s;
	}
	private String notificationMethod(String receivedMessage) {
		// TODO Auto-generated method stub
		if(db.makeConnection('!', receivedMessage))
			return db.returnMessageToReturn();
		else
			return "Error in commands";
			
	}
	private String workWithTimetable(String receivedMessage) {
		String s; 
		
		if(db.makeConnection('*', receivedMessage))
			s = db.returnMessageToReturn();
		else
			s = "Error work with timetables";
		
		
		// TODO if (*editTimetable;) find timetable, and update it 
		// TODO if (*removeTimetable;) find timetable and remove it 
		return s;
	}
	/**
	 * method is looking for the number of string in the message received from the client
	 * @param s = message from client 
	 * @param count = number of need string 
	 * @return
	 */
	private String showStringNumber(String s, int count){
		int countOfSeparator =0;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length() ; i++){
			if(s.charAt(i) == ';')
				countOfSeparator++;
			else if(s.charAt(i) != ';' && countOfSeparator == count){
				
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param nameOfMedicine
	 */
	private String LookForNameOfMedicineInDatabase(String nameOfMedicine){
		if(db.makeConnection('%', nameOfMedicine)){
			//System.out.println("returning "+db.returnMessageToReturn());
			return db.returnMessageToReturn();
			//return "medicine found";
		}
		else{
			//System.out.println("returning medicine does not exists in DB");
			return "%medicineNames;";
		}
			
		
		//return "error";
	}
	/**
	 * this method has commands for the registration new users to the system
	 * @param s
	 * @return
	 */
	private String commandsForRegistration(String s){


			if(!db.makeConnection('~', s)){ 
				if(db.makeConnection('$', s));
					s = "registered;"+this.showStringNumber(s, 1)+";"+this.showStringNumber(s, 2)+";";	
				}
			else
				s = "User name is already exists";
		
		return s;
	}
	/**
	 * This method has the commands for the sign in into the system 
	 * @param s
	 * @return
	 */
	private String commandsForSignIn(String s){
		
		
		if(db.makeConnection('~', s))
			if(db.returnMessageToReturn().contains("~id;")){
			s = "Sign In"+db.returnMessageToReturn();
			}else if(db.returnMessageToReturn().contains("~idUser;")){
				s = db.returnMessageToReturn();
			}
		else
			s = "You should registered";
		return s;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	private String commandForEditProfile(String s){
		
			if(db.makeConnection('#', s))
				s = db.returnMessageToReturn();
			else
				s = "User name is already exists";
		return s;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	private String getpersonalDataFromDB(String s){
		/*message form "-ShowMeById;id;"*/
		if(user == null)
			if(db.makeConnection('-', s))
				user = db.returnPersonFromDB();
		//}else{
			Person contactPerson = user.returnContactPerson();
			
			if(s.contains("-infoUser;"))
				s = "-infoUser;"+user.returnID()+";"+user.returnName()+";"+user.returnSurname()+";"+user.returnSex()+";"+user.returnTelNumber()+";";
			else if(s.contains("-infoContactPerson;"))
				s = "-infoContactPerson;"+contactPerson.returnID()+";"+contactPerson.returnName()+";"+contactPerson.returnSurname()+";"+contactPerson.returnSex()+";"+contactPerson.returnTelNumber()+";";
			//else if(s.contains("-infoTimetables"))
				//TODO
			//else if(s.contains("-infoMedicine"))
			//	s = "-infoMedicine"+
	//	}
		return s;
	}
}