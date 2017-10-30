/**
 * Class to define a rule
 * 
 * @author Shreya Vishwanath Rao
 * @version 1.0: 2017/09/30
 *
 */

public class Rule {

	String from;
	String to;
	String prev;
	
	/**
	 * Constructor of the class
	 * 
	 * @param f :from tag
	 * @param t : to tag
	 * @param p : previous tag
	 */
	public Rule(String f, String t, String p){
		from=f;
		to=t;
		prev=p;
	}
	
	/**
	 * Dispalys the rule
	 */
	public void print(){
		System.out.println("From " + from + " To "+ to + " when prev is " + prev);
	}

	/*Gets the from tag*/
	public String getFrom() {
		return from;
	}

	/*Sets the from tag*/
	public void setFrom(String from) {
		this.from = from;
	}

	/*Gets the to tag*/
	public String getTo() {
		return to;
	}

	/*Sets the to tag*/
	public void setTo(String to) {
		this.to = to;
	}

	/*Gets the previous tag*/
	public String getPrev() {
		return prev;
	}

	/*Sets the previous tag*/
	public void setPrev(String prev) {
		this.prev = prev;
	}
}
