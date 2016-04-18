package person;
/**
 * 
 */

/**
 * @author juraj
 *
 */
public class medicine {

	private int ID;
	private String nameOfMedicine;
	private String Strength;
	private String DayLimit;
	private String DaysLimit;
	/**
	 * constructor 
	 */
	public medicine(){
		
	}
	/**
	 * 
	 * @param id
	 */
	public void setID(int id){
		this.ID = id;
	}
	
	/**
	 * 
	 * @param nameOfmedicine
	 */
	public void setNameOfMedicine(String nameOfmedicine){
		this.nameOfMedicine = nameOfmedicine;
	}
	
	/**
	 * 
	 * @param strength
	 */
	public void setStrength(String strength){
		this.Strength = strength;
	}
	
	/**
	 * 
	 * @param dayLimit
	 */
	public void setDayLimit(String dayLimit){
		this.DayLimit = dayLimit;
	}
	
	/**
	 * 
	 * @param daysLimit
	 */
	public void setDaysLimit( String daysLimit){
		this.DaysLimit = daysLimit;
	}
	
	/**
	 * 
	 * @return
	 */
	public int returnIDOfMedicine(){
		return this.ID;
	}
	
	/**
	 * 
	 * @return
	 */
	public String returnNameOfMedicine(){
		return this.nameOfMedicine;
	}
	
	/**
	 * 
	 * @return
	 */
	public String returnStrengthOfMedicine(){
		return this.Strength;
	}
	
	/**
	 * 
	 * @return
	 */
	public String returnDayLimit(){
		return this.DayLimit;
	}
	
	/**
	 * 
	 * @return
	 */
	public String returnDaysLimit(){
		return this.DaysLimit;
	}
}
