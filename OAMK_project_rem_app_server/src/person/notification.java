package person;

import main_Server.OurDateClass;

/**
 * 
 * @author juraj
 *
 */
public class notification {
	
	 private String date, time, status;
	
	
	 public notification(){
		 
	 }
	 
	 notification(String date, String time, String status){
		 this.date = date;
		 this.time = time;
		 this.status = status;
	}
	
	public void setNotificationDate(String d){
		this.date = d;
	}
	
	public void setNotificationTime(String t){
		this.time = t;
	}
	
	public void setNotificationStatus(String s){
		this.status = s;
	}
	
	public String returnNotificationDate(){
		return this.date;
	}
	
	public String returnNotificationTime(){
		return this.time;
	}
	
	public String returnNotificationStatus(){
		return this.status;
	}

	
	public String returnNotificationTimePlusMin(int min){
		OurDateClass d = new OurDateClass();
		d.setTimeFromTimeformat(time);
		d.setMinutes(min + d.returnMinutes());
		
		return d.returnTime();
	}
}
