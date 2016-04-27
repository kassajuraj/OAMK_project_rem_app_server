package person;
import java.sql.Date;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author juraj
 *
 */
public class medicineTimetables {

	private int ID;
	private medicine medicine = new medicine();
	private String dateFrom, dateUntil, timeFrom, timeUntil;
	private String active;
	
	private ArrayList<notification> notificationsList= new ArrayList<notification>();
	
	
	/**
	 * constructor
	 */
	public medicineTimetables(){

	}
	
	/**
	 * 
	 * @return
	 */
	public int returnIdOfTimetable(){
		return this.ID;
	}
	/*
	public void setID(int n){
		this.ID = n;
	}
	*/
	public void setIDOfTimetable(int id){
		this.ID = id;
	}
	public String returnActive(){
		return this.active;
	}
	
	public ArrayList<notification> returnArrayListOfNotificaations(){
		return notificationsList;
	}
	
	public void setActive(String s){
		active = s;
	}
	
	/**
	 * 
	 * @return
	 */
	public medicine returnMedicine(){
		return this.medicine;
	}
	/**
	 * 
	 * @param s
	 * @return
	 */
	public String returnDate(String s){
		if(s.equals("from"))
			return this.dateFrom;
		else if(s.equals("to"))
			return this.dateUntil;
		
		return null;
	}
	/**
	 * 
	 * @param s
	 * @return
	 */
	public String returnTime(String s){
		if(s.equals("from"))
			return this.timeFrom;
		else if(s.equals("to"))
			return this.timeUntil;
		
		return null;
	}
	/**
	 * 
	 * @param arg
	 * @param string
	 */
	public void setDate(String arg, String string){
		if(arg.equals("from"))
			this.dateFrom = string;
		else if(arg.equals("to"))
			this.dateUntil = string;	
	}
	/**
	 * 
	 * @param arg
	 * @param string
	 */
	public void setTime(String arg, String string){
		if(arg.equals("from"))
			this.timeFrom = string;
		else if(arg.equals("to"))
			this.timeUntil = string;
		
		
	}
	/**
	 * 
	 * @param med
	 */
	public void setMedicine(medicine med) {
		this.medicine = med; 
		
	}
}
