package person;

import main_Server.OurDateClass;

/**
 * 
 * @author juraj
 *
 */
public class notification {
	
	 private String date, time, status;
	 int ID_timetable;
	
	/**
	 * Constructor
	 */
	 public notification(){
		 
	 }
	 /**
	  * 
	  * @param date
	  * @param time
	  * @param status
	  */
	 notification(String date, String time, String status){
		 this.date = date;
		 this.time = time;
		 this.status = status;
	}
	/**
	 * 
	 * @param id
	 */
	 public void setID_table(int id){
		 this.ID_timetable = id;
	 }
	 /**
	  * 
	  * @return
	  */
	 public int returnIdTimetable(){
		 return this.ID_timetable;
	 }
	 /**
	  * 
	  * @param d
	  */
	public void setNotificationDate(String d){
		this.date = d;
	}
	/**
	 * 
	 * @param t
	 */
	public void setNotificationTime(String t){
		this.time = t;
	}
	/**
	 * 
	 * @param s
	 */
	public void setNotificationStatus(String s){
		this.status = s;
	}
	/**
	 * 
	 * @return
	 */
	public String returnNotificationDate(){
		return this.date;
	}
	/**
	 * 
	 * @return
	 */
	public String returnNotificationTime(){
		return this.time;
	}
	/**
	 * 
	 * @return
	 */
	public String returnNotificationStatus(){
		return this.status;
	}
	/**
	 * 
	 * @param min
	 * @return
	 */
	public String returnNotificationTimePlusMin(int min){
		OurDateClass d = new OurDateClass();
		d.setTimeFromTimeformat(time);
		int m = min + d.returnMinutes();
		while(m >= 60){
			m = m-60;
			d.plusHoursToDate(1);
		}
		d.setMinutes(m);
		
		return d.returnTime();
	}
}
