import java.util.HashMap;

/**
 * Creates a class to contain the details of each word in the corpus
 * 
 * @author Shreya Vishwanath Rao
 * @version 1.0: 2017/09/30
 *
 */ 
public class Word {
	String element; //word in the corpus
	HashMap<String,Integer> taggedCount; //word tags and their respective count
	int total; //total number of instances of the word
	String bestTag; //best tag for the word
	
	/**
	 * Constructor of the class
	 */
	public Word(){
		element=null;
		taggedCount= new HashMap<String,Integer>();
		total=0;
	}
	
	/**
	 * Parameterized constructor of the class
	 */
	public Word(String token){
		element=token;
		taggedCount=new HashMap<String,Integer>();
		total=0;
	}
	
	/**
	 * Sets or increments the tag count of the word
	 * 
	 * @param tag: the tag of the word
	 */
	public void putTaggedCount(String tag){
		int count=taggedCount.getOrDefault(tag,0);
		taggedCount.put(tag,count+1);
		total++;
	}
	
	/**
	 * Gets the most probable tag (Highest probability) of the word
	 * @return : the best tag
	 */
	public String getMostProb(){
		String bestTag="AA";
		double probMax=0;
		for(String t:taggedCount.keySet()){
			double prob= (double)taggedCount.get(t)/(double)total;
			if(prob>probMax){
				bestTag=t;
				probMax=prob;
			}
		}
		return bestTag;
	}
	
}
