//package serverPart;
package main_Server;
import java.sql.*;

import person.Person;
import person.medicine;
import person.medicineTimetables;
import person.notification;
/**
 * 
 * @author juraj
 *
 */
public class database {
	

	private Connection conn = null;
	private Statement stmt;
	private String messageToReturn ="blabla";
	private String url = "jdbc:mysql://127.0.01:3306/Remmem_app?useSSL=false";
	private   String username = "root";
	private   String password = "jurko14";
	private Person person = new Person();
	
	private String loginMail = "", loginPassword ="";
	
	/**
	 * constructor 
	 */
	database(){
		
	}
	/**
	 * method which creating the connection to database called Remmem_app
	 */
	public boolean makeConnection(char ch, String receivedMessage){
		  boolean b = false;
		   System.out.println("Connecting database...");
		   System.out.println("Loading driver...");
		   try (Connection connection = DriverManager.getConnection(url, username, password)) {
		       conn = connection;
			   System.out.println("Database connected!");
			   System.out.println("Creating statement...");
			   stmt = conn.createStatement();
			   System.out.println("Statement has been created...");

			/*calling the method which is looking for the medicine name 
			 * this method should be called everytime when the input string name will be changed */
			   //TODO call this method everytime when the name of medicine will be changed 
 
			   b = selectActionWithDB(ch, receivedMessage);
			   if(b==true)
				   this.CloseConnectionAndStatment();
			   
		   } catch (SQLException e) {
		       throw new IllegalStateException("Cannot connect the database!", e);
		   }
		   try {
		       Class.forName("com.mysql.jdbc.Driver");
		       System.out.println("Driver loaded!");
		   } catch (ClassNotFoundException e) {
		       throw new IllegalStateException("Cannot find the driver in the classpath!", e);
		   }
		   System.out.println("Goodbye!");
		   
		   return b;
	}
	/**
	 * This method is returning the message it is the way how to read message outside this class
	 * @return message to client  
	 */
	public String returnMessageToReturn(){
		return this.messageToReturn;
	}
	/**
	 * 
	 * @param ReceivedLoginMessage
	 */
	private void divideLoginMessage(String ReceivedLoginMessage){
		loginMail = this.showStringNumber(ReceivedLoginMessage, 1);
	    loginPassword = this.showStringNumber(ReceivedLoginMessage, 2);
	}
	/**
	 * 
	 * @param p
	 * @param receivedPersonDataMessage
	 */
	private void dividePersonDataMessage(Person p, String receivedPersonDataMessage){

		p.setIdPassword(Integer.parseInt(this.showStringNumber(receivedPersonDataMessage, 1)));
		p.setName(this.showStringNumber(receivedPersonDataMessage, 2));
		p.setSurname(this.showStringNumber(receivedPersonDataMessage, 3));
		p.setSex(this.showStringNumber(receivedPersonDataMessage, 4));
		p.setTelNumber(this.showStringNumber(receivedPersonDataMessage, 5));
	
	}
	
	/**
	 * This method is called when the connection to databse is successful and inside the method is way how to chose right action with incoming message
	 * @param symbol = first symbol of message helps to make disicion 
	 * @param s = all message received from client 
	 * @return true = all action were right, false = was some error during the action 
	 * @throws SQLException
	 */
	private boolean selectActionWithDB(char symbol, String s)throws SQLException{

		boolean AllRight = false;
		switch(symbol){
		
		case '~' : AllRight = this.isLoginInfoExists(s); break;
		case '!' : AllRight = this.notificationInDatabase(s); break;
		case '$' : AllRight = this.addRegistrationData(s); break;
		case '#' : AllRight = this.addPersonalData(s); break; 
		case '%' : AllRight = this.LookForMedicineName(s); break;
		case '-' : AllRight = this.recordAllUsersData(s); break;
		case '*' : AllRight = this.workWithTimetablesInDatabase(s); break;
		default : //TODO invalid action
		}
		return AllRight;
	}
	private boolean notificationInDatabase(String s) {
		
		if(s.contains("!addNotification;"))
			if(this.insertNotificationsToDatabase(s))
				return true;

		return false;
	}
	private boolean insertNotificationsToDatabase(String receiveMessage) {
		/*"!addNotification;timetableId;notificationDate;notificationTime;notificationStatus;"*/
		notification n = new notification();
		medicineTimetables lastTimetable = person.returnTimetable(person.returnArrayListOfTimetables().size()-1);
		if(lastTimetable.returnIdOfTimetable() == Integer.parseInt(this.showStringNumber(receiveMessage, 1))){
				//System.out.println("Id of table is the same");
		
			n.setNotificationDate(this.showStringNumber(receiveMessage, 2));
			n.setNotificationTime(this.showStringNumber(receiveMessage, 3));
			n.setNotificationStatus(this.showStringNumber(receiveMessage, 4));
		
			System.out.println("inserting new notification to timetable");
			try{
				String sql = "INSERT INTO `remmem_app`.`notification_timetable` (`date`, `time`, `status`, `id_timetable`) VALUES ('"+n.returnNotificationDate()+"', '"+n.returnNotificationTime()+"', '"+n.returnNotificationStatus()+"', '"+lastTimetable.returnIdOfTimetable()+"');";
				stmt.executeUpdate(sql);
			}catch(SQLException e){
				System.out.println("Cannot insert the notification SQl exception "+e);
				return false;
			}
			lastTimetable.returnArrayListOfNotificaations().add(n);
			messageToReturn = "notification has been add";
		}
		
		return true;
	}
	
	private boolean workWithTimetablesInDatabase(String s) {
		boolean b = false;
		
		if(s.contains("*addTimetable;")){
				b = this.insertNewTimetable(s);
				messageToReturn = "*timetableCreated;"+person.returnID()+";"+person.returnTimetable(person.returnArrayListOfTimetables().size()-1).returnIdOfTimetable()+";";
		}
		// TODO if (*editTimetable;) find timetable, and update it 
		// TODO if (*removeTimetable;) find timetable and remove it 
				
		return b;
	}
	
	private boolean insertNewTimetable(String message) {
		/*message form "*addTimetable;IDUser;medicineName;medicineStrength;dateFrom;timeFrom;dateUntil;timeUntil;"*/
		boolean inserted = false;
		
		medicine pills = new medicine();
		pills.setNameOfMedicine(showStringNumber(message, 2));
		pills.setStrength(this.showStringNumber(message, 3));
		pills = this.fillPillInfo(pills);
		//
		medicineTimetables table = new medicineTimetables();
		table.setDate("from", this.showStringNumber(message, 4));
		table.setTime("from", this.showStringNumber(message, 5));
		table.setDate("to", this.showStringNumber(message, 6));
		table.setTime("to", this.showStringNumber(message, 7));
		table.setMedicine(pills);
		if(this.insertNewTimetable(table))
			System.out.println("table has been added ");
		
		if(this.timetableExists(table))
			inserted = true;
		else 
			inserted = false;
		
		person.returnArrayListOfTimetables().add(table);
		
		return inserted;
	}
	
	private boolean timetableExists(medicineTimetables table) {
		
		System.out.println("looking for the ID of timetable");
		int id = 0;
		String sql = "SELECT id_timetable FROM remmem_app.all_timetables where id_user = '"+person.returnID()+"' and id_medicine = '"+table.returnMedicine().returnIDOfMedicine()+"' and date_from = '"+table.returnDate("from")+"' and time_from = '"+table.returnTime("from")+"' and date_until = '"+table.returnDate("to")+"' and time_until = '"+table.returnTime("to")+"'";
		try{
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				id = rs.getInt("id_timetable");
				if( id != 0){
					table.setIDOfTimetable(id);
				return true;
				}
			}
		}catch(SQLException e){
			System.out.println("Id not found SQL exception "+e);
			return false;
		}
		return false;
	}
	
	private boolean insertNewTimetable(medicineTimetables table) {
		
		System.out.println("inserting new timetable");
		String active = "active";
		try{
		String sql = "INSERT INTO `remmem_app`.`all_timetables` (`id_user`, `id_medicine`, `date_from`, `time_from`, `date_until`, `time_until`, `active`) VALUES ('"+person.returnID()+"', '"+table.returnMedicine().returnIDOfMedicine()+"', '"+table.returnDate("from")+"', '"+table.returnTime("from")+"', '"+table.returnDate("to")+"', '"+table.returnTime("to")+"', '"+active+"');";
		stmt.executeUpdate(sql);
		}catch(SQLException e){
			System.out.println("Cannot insert the table SQl exception "+e);
			return false;
		}
		return true;
	}
	
	
	private medicine fillPillInfo(medicine pills) {
		
		System.out.println("reading all data about medicine");
		String sql = "SELECT id_medicine, DrugName, Strength, Limit_Day, Limit_Days FROM remmem_app.medicinelist where DrugName = '"+pills.returnNameOfMedicine()+"' and Strength = '"+pills.returnStrengthOfMedicine()+"'";
		try{
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
   	  	
				pills.setID(rs.getInt("id_medicine"));
				pills.setNameOfMedicine(rs.getString("DrugName"));
   	  			pills.setStrength(rs.getString("Strength"));
   	  			pills.setDayLimit(rs.getString("Limit_Day"));
   	  			pills.setDayLimit(rs.getString("Limit_Days"));
			}
   	  		rs.close();
		}catch(SQLException e){
			System.out.println("Cannot find the medicine SQl exception "+e);
		}
		
		return pills;
	}

	/**
	 * Method which is searching for the medicine in the database 
	 * @param stringFromTextField name of medicine or part of name 
	 * @return true if find some medicine and false if not found 
	 * @throws SQLException
	 */
	private boolean LookForMedicineName(String stringFromTextField) throws SQLException{
		
		boolean founded = false;
		String sql = "SELECT DrugName, Strength FROM Remmem_app.medicineList where DrugName like '"+stringFromTextField+"%'";
	    ResultSet rs = stmt.executeQuery(sql);
	    messageToReturn = "%medicineNames;";
	    String MedicineName = "null";
	      while(rs.next()){
		         /*Retrieve by column name*/
	    	  //	rs.getString("DrugName");
	    	  	MedicineName = rs.getString("DrugName")+"~~"+rs.getString("Strength"); 
		        messageToReturn = messageToReturn+ MedicineName+";";
		         	if(MedicineName != "null")
		         		founded = true;
		      }
		     rs.close();
	//      System.out.println(founded);
	//	      System.out.println(messageToReturn); 
	 return founded;
	}
	
	/**
	 * method is adding the login's data to table "reginfolist" (where are save the login and password)
	 * @param messageFromRegister form "$RegMe;mail;password;"
	 * @return true = data has been added, false = data has not been added
	 */
	private boolean addRegistrationData(String messageFromRegister){
		
		this.divideLoginMessage(messageFromRegister);		
		System.out.println("Inserting records into the table 'reginfolist'...");
	      	try {
	      		String sql = "INSERT INTO `Remmem_app`.`reginfolist` (`email`, `password`) VALUES ('"+loginMail+"', '"+loginPassword+"');";
	      		stmt.executeUpdate(sql);
	    	  	System.out.println("Data has been added...");
	      		} catch (SQLException e) {
	      			System.out.println("System cannot insert the registration information (mail, password)");
	      			return false;
	      		}
		return true;
	}
	/**
	 * Method is looking for the login information in the table for registration "reginfolist"
	 * @param messageFromSignIn form "~SignIn;mail;password;"
	 * @return TRUE = mail and password is exists otherwise FALSE
	 * @throws SQLException
	 */
	private boolean isLoginInfoExists(String messageFromSignIn) throws SQLException{
		
		this.divideLoginMessage(messageFromSignIn);
		int idUser = 0;
		String sql = "SELECT idUser FROM remmem_app.reginfolist where email = '"+loginMail+"' and password = '"+loginPassword+"'";
		ResultSet rs = stmt.executeQuery(sql);
		
		if (rs.next()) {
		    idUser = rs.getInt("idUser");
		    if (idUser != 0){ 
		    	if(!this.wasUserProfilCreated(messageFromSignIn))
		    		System.out.println("User's profile was not created yet");
		    	return true;
		    }
		}
		return false;
	}
	/**
	 * Methos is looking for the information from login and user's profile   
	 * @param messageFromSignIn form "~SignIn;mail;password;"
	 * @return TRUE = if the user's profile was created (messageToReturn "~id;")and it was added to "userslist" table, FALSE otherwise (messageToReturn "~idUser;")
	 * @throws SQLException
	 */
	private boolean wasUserProfilCreated(String messageFromSignIn) throws SQLException{
		
		int id = 0;
		String sql = "SELECT id, id_user_sign_in FROM remmem_app.userslist where id_user_sign_in = (select idUser FROM remmem_app.reginfolist where email = '"+loginMail+"' and password = '"+loginPassword+"')";
		ResultSet rs = stmt.executeQuery(sql);
		
		if (rs.next()) {
		    id = rs.getInt("id");
		    if (id != 0){ 
		    	messageToReturn = "~id;"+id;
		    	//TODO insert ID to User's id in user's list 
		    	return true;
		    }
		}else{
		    String sql1 = "select idUser FROM remmem_app.reginfolist where email = '"+loginMail+"' and password = '"+loginPassword+"'";
			ResultSet rs1 = stmt.executeQuery(sql1);
				if (rs1.next()) {
				    id = rs1.getInt("idUser");
				    if (id != 0) 
				    	messageToReturn = "~idUser;"+id;
			}
		 }
		return false;
	}
	
	/**
	 * Method is adding the personal data about users or user's contact persons (name, surname, sex, tel.number) 
	 * @param messageFromEditOwnProfile form "#EditMe;id from registration;name;surname;sex;tel.number" or "#EditContactPerson;id from registration;name;surname;sex;tel.number"
	 * @return TRUE = profile has been edited, FALSE = record cannot be insert because there is some SQL exception 
	 */
	private boolean addPersonalData(String messageFromEditOwnProfile){
		
		if(this.showStringNumber(messageFromEditOwnProfile, 0).equals("#EditMe")){			
			this.dividePersonDataMessage(person, messageFromEditOwnProfile);
				System.out.println("Inserting records into the table...");				
				try {
					String sql = "INSERT INTO `remmem_app`.`userslist` (`id_user_sign_in`, `name`, `surname`, `sex`, `tel_Number`) VALUES ('"+person.returnIdPassword()+"', '"+person.returnName()+"', '"+person.returnSurname()+"', '"+person.returnSex()+"', '"+person.returnTelNumber()+"')";
					stmt.executeUpdate(sql);
					System.out.println("Inserted records into the table...");
					person.setID(this.returnIDFromPerson());
					messageToReturn = "#EditMeDone;"+person.returnID();
				} catch (SQLException e) {
					System.out.println("System cannot insert the User's data");
					return false;
				}
		}else if(this.showStringNumber(messageFromEditOwnProfile, 0).equals("#EditContactPerson")){
			/* Edit contact person at first look for contact person if exists return id else add new contact person return id*/
			person.setContactPerson(new Person());
			this.dividePersonDataMessage(person.returnContactPerson(), messageFromEditOwnProfile);
			try{
				/*add id of contact person to the profile of user*/
				int contactPersonID = 0;
					contactPersonID = this.isContactPersonExists(/*messageFromEditOwnProfile*/); 
					/*if contact person is not exists (contactPersonID == 0) then make new contact person and return the ID of this new contact person*/		
					if(contactPersonID == 0){
						this.makeContactPerson(/*messageFromEditOwnProfile*/); 
						contactPersonID = returnIdFromContactPerson(/*messageFromEditOwnProfile*/);
					}
					/*update the "userslist" table */
					System.out.println("Updating id of Contact in the 'userslist' table...");
					String sqlI = "update `remmem_app`.`userslist` set `id_contactPerson` = '"+contactPersonID+"' where id ='"+person.returnID()+"'";
						stmt.executeUpdate(sqlI);
							messageToReturn = "#EditContactPersonDone;"+person.returnID();
							System.out.println("Record has been updated...");
			}catch(SQLException e){
				System.out.println("Cannot edit Contact Person data");
			}
		}
		return true;
	}
	
	/**
	 * Method is looking for the contact person in database 
	 * @param messageFromEditContactPerson form "#EditContactPersonDone;id;name;surname;sex;tel.number;"
	 * @return ID of contact person 
	 */
	private int isContactPersonExists(){
		int id = 0;
		/*look for the contact person using all atributes in contact person table*/
		try{
			String sql = "SELECT id_ContactPerson FROM remmem_app.contactpersonlist where name = '"+person.returnContactPerson().returnName()+"' and surname = '"+person.returnContactPerson().returnSurname()+"' and sex = '"+person.returnContactPerson().returnSex()+"' and tel_number = '"+person.returnContactPerson().returnTelNumber()+"'";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				id = rs.getInt("id_ContactPerson");
				if (id != 0)
					System.out.println("contact person has been found");
			}
		}catch(SQLException e){
			System.out.println("SQL eror finding contact person "+e);
		}
		    return id;
	}
	
	/**
	 * method is making the new contact person in "contactpersonlist" table firstly it is looking for the atributes in "userslist" if find some user then insert this user to "contactPersonlist: else make new contact person list
	 * @param messageFromEditContactPerson form "#EditContactPerson;id;nameContactPerson;surnameContactPerson;sexContactPerson;tel.NumberContactPerson"
	 */
	private void makeContactPerson(){

		try{
			/*if not found then insert new contact person*/
			System.out.println("Inserting records into the 'contactpersonlist' table...");
			String sqlI = "INSERT INTO `remmem_app`.`contactpersonlist` (`name`, `surname`, `sex`, `tel_number`) VALUES ('"+person.returnContactPerson().returnName()+"', '"+person.returnContactPerson().returnSurname()+"', '"+person.returnContactPerson().returnSex()+"', '"+person.returnContactPerson().returnTelNumber()+"')";
			stmt.executeUpdate(sqlI);
			System.out.println("Contact person has been inserted...");
		}catch(SQLException e){
			System.out.println("SQL error inserting the contactPerson "+e);
		}
	}
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int returnIDFromPerson() throws SQLException{
		int id = 0;
		String sql = "SELECT id FROM remmem_app.userslist where id_user_sign_in= '"+person.returnIdPassword()+"' and name = '"+person.returnName()+"' and surname = '"+person.returnSurname()+"' and sex = '"+person.returnSex()+"' and tel_Number='"+person.returnTelNumber()+"'";
		ResultSet rs = stmt.executeQuery(sql);		
		if (rs.next()) {
		    id = rs.getInt("id");
		    if (id != 0){
		    	System.out.println("User's ID found "+ id);
		    }
		}
		
		return id;
	}
	
	/**
	 * Method is looking for the contact person and returning the ID of contact person 
	 * @param messageFromEditContactPerson form "#EditContactPerson;id;nameContactPerson;surnameContactPerson;sexContactPerson;tel.NumberContactPerson"
	 * @return ID of contact person in the table "contactpersonlist"
	 */
	private int returnIdFromContactPerson(){
		int id = 0;		
		try{
			/*look for the id after inserting the new contact person to the contactPerosnList*/
			String sqlS = "SELECT id_ContactPerson FROM remmem_app.contactpersonlist where name = '"+person.returnContactPerson().returnName()+"' and surname = '"+person.returnContactPerson().returnSurname()+"' and sex = '"+person.returnContactPerson().returnSex()+"' and tel_number = '"+person.returnContactPerson().returnTelNumber()+"'";
			ResultSet rsS = stmt.executeQuery(sqlS);
			if (rsS.next()) {
			    id = rsS.getInt("id_ContactPerson");
			    System.out.println(id);
			    if (id != 0) 
			    	System.out.println("System is returning the id");
			}
			}catch(SQLException e){
				System.out.println("System cannot select ID of contact person"+e);
			}
			return id;
	}
	
	/**
	 * 
	 * @param message
	 */
	private boolean recordAllUsersData(String message){
		
		int ID = (Integer.parseInt(this.showStringNumber(message, 1)));
		person.setID(ID);
		try{
			String sql = "SELECT id, name, surname, sex, tel_number FROM remmem_app.userslist where id = '"+person.returnID()+"'";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				//ID = rs.getInt("id");
				person.setName(rs.getString("name"));
				person.setSurname(rs.getString("surname"));
				person.setSex(rs.getString("sex"));
				person.setTelNumber(rs.getString("tel_number"));
			}
			/*record the contact person using the ID of user*/
			this.recordContactPersonData(ID);
			/*record medicine timetables using the ID of user*/
			this.recordMedicineTimetableData(ID);
			/*record the medicine and the notifications to timetable using loop for all medicineTimetables in person's profile */
			for(medicineTimetables mtt : person.returnArrayListOfTimetables()){
				mtt.setMedicine(this.recordMedicine(mtt.returnMedicine().returnIDOfMedicine()));	
				this.recordNotifications(mtt);
			}
		}catch(SQLException e){
			System.out.println("error Cannot read data to person "+e);
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param id
	 * @throws SQLException
	 */
	private void recordContactPersonData(int id) throws SQLException{
		if(person.returnContactPerson() == null)
			person.setContactPerson(new Person());
		
		Person contactPerson = person.returnContactPerson();
		
		String sql = "SELECT * FROM remmem_app.contactpersonlist where id_ContactPerson = (SELECT id_ContactPerson FROM remmem_app.userslist where id = '"+id+"')";
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()){
			contactPerson.setID(rs.getInt("id_ContactPerson"));
			contactPerson.setName(rs.getString("name"));
			contactPerson.setSurname(rs.getString("surname"));
			contactPerson.setSex(rs.getString("sex"));
			contactPerson.setTelNumber(rs.getString("tel_number"));
		}
	}
	
	/**
	 * 
	 * @param ID
	 */
	private void recordMedicineTimetableData(int ID){
		//TODO
		//int idMedicine;
		try{
			//String sql = "SELECT id_timetable, id_user, id_medicine, date_from, time_from, date_until, time_until, active FROM remmem_app.all_timetables where id_user = '"+ID+"'";
			String sql = "SELECT * FROM remmem_app.all_timetables where id_user = '"+ID+"'";
			ResultSet rs = stmt.executeQuery(sql);
		
			while (rs.next()){
				medicineTimetables timetable = new medicineTimetables();
				timetable.setIDOfTimetable(rs.getInt("id_timetable"));
				timetable.returnMedicine().setID(rs.getInt("id_medicine"));
				timetable.setDate("from", rs.getString("date_from"));
				timetable.setTime("from", rs.getString("time_from"));
				timetable.setDate("to", rs.getString("date_until"));
				timetable.setTime("to", rs.getString("time_until"));
				timetable.setActive(rs.getString("active"));
			
		//	this.recordNotifications(timetable);
		//	medicine m = this.recordMedicine(idMedicine);	
		//	timetable.setMedicine(m);
		//	System.out.println("Pridavam tabulku "+timetable.returnIdOfTimetable());
				person.returnArrayListOfTimetables().add(timetable);
				}
			}catch(SQLException e){
			System.out.println("timetable chyba "+e);
		}
	}
	
	private void recordNotifications(medicineTimetables timetable){
		int idTimetable = timetable.returnIdOfTimetable();
		
		try{
		String sql = "SELECT * FROM remmem_app.notification_timetable where id_timetable = '"+idTimetable+"'";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()){
			notification not = new notification();
			not.setNotificationDate(rs.getString("date"));
			not.setNotificationTime(rs.getString("time"));
			not.setNotificationStatus(rs.getString("status"));
			
		//	System.out.println("adding notification date "+ not.returnNotificationDate()+ " time "+not.returnNotificationTime()+" from table id"+idTimetable);
			timetable.returnArrayListOfNotificaations().add(not);
		}
		}catch(SQLException e){
			System.out.println("notification chyba "+e);
		}
	}
	
	
	private medicine recordMedicine(int int1){
		medicine med = null; 
	try{
		String sql = "SELECT * FROM remmem_app.medicinelist where id_medicine = '"+int1+"';";
				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()){
					med = new medicine();
					med.setID(rs.getInt("id_medicine"));
					med.setNameOfMedicine(rs.getString("DrugName"));
					med.setStrength(rs.getString("Strength"));
					med.setDayLimit(rs.getString("Limit_Day"));
					med.setDaysLimit(rs.getString("Limit_Days"));
					
			//		System.out.println("nahravam liek "+med.returnNameOfMedicine());
				}
	}catch(SQLException e){
		System.out.println("medicine chyba "+e);
	}
		return med;
	}
	
	/**
	 * 
	 * @return
	 */
	public Person returnPersonFromDB(){
		return this.person;
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
			else if(s.charAt(i) != ';' && countOfSeparator == count)				
				sb.append(s.charAt(i));
		}
		return sb.toString();
	}
	
	/**
	 * method which close the connection between the server and database 
	 * @return
	 */
	public boolean CloseConnectionAndStatment(){
		   
	      try {
			stmt.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			System.out.println("Cannot close connection to database");
			e.printStackTrace();
		}
	   return false;
	}
	
}