import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Implements transformation based tagging on a given sentence, based on 
 * a tagged corpus
 * 
 * @author Shreya Vishwanath Rao
 * @version 1.0: 2017/09/30
 *
 */

public class Brills {
	static HashMap<Rule,Integer> rules;
	static ArrayList<String> tags;
	static FileWriter writer;

	/**
	 * Main function. Reads a tagged corpus from the user and determines the
	 * most probable tag for each word. Further, writes the information into
	 * another file.
	 * 
	 * @param args : Command Line arguments
	 * @throws IOException : Thrown when unable to open file or file doesn't exists
	 */
	public static void main(String[] args) throws IOException{
		String corpusfile= null; //File containing the tagged corpus
		String tagfile="tag.txt"; //file of all tags
		String outfile="Output.txt"; // Final output file
		String fileO= null; //File containing the given sentence
		String fileG= null; //File containing the given tagged sentence
		
		if(args.length>0){
			corpusfile=args[0];
			fileO=args[1];
			fileG=args[2];
		}
		
		writer= new FileWriter(new File(outfile));
		
		Scanner corpusIn= new Scanner(new File(corpusfile));
		Scanner tag= new Scanner(new File(tagfile));
		Scanner inO= new Scanner(new File(fileO));
		Scanner inG= new Scanner(new File(fileG));
		
		//reads the corpus and finds the most probable tag of each word
		ArrayList<String> corpus= getText(corpusIn);
		HashMap<String, String> mostProbTag=POSTagging.mostProb(corpus);
		
		//read the tags
		readTags(tag);
		
		//gets the set of all rules
		getBestInstance(corpus,mostProbTag);
		
		//Creates a Priority Queue to obtain the best rule
		PriorityQueue<Rule> pq= new PriorityQueue<Rule>(rules.size(), new MaxComparator());
		for(Rule r:rules.keySet()){
			pq.add(r);
		}
		
		/* Applies the rules on the given sentence and compares it with the
		 * correct tags to find the error
		 */
		double error = checkError(inO,inG, mostProbTag,pq);
		String str= "With Rules, Error: "+ error+"\n";
		writer.write(str);
		
		writer.close();
		
	}
	
	/**
	 * 
	 * @param corpus
	 * @param mostProb
	 */
//	private static void findRules(ArrayList<String> corpus, HashMap<String, String> mostProb) {
//	
//		ArrayList<Rule> rules= new ArrayList<Rule>();
//		for(String sentence:corpus){
//			String[] words= sentence.split("[\n\t ]");
//			for(int i=1;i<words.length;i++){
//				String[] tok= words[i].split("_");
//				String old=mostProb.get(tok[0]);
//				if(!tok[1].equals(old)){
//					String prev = words[i-1].split("_")[1];
//					Rule r= new Rule(old,tok[1], prev);
//					if(!rules.contains(r)){
//						rules.add(r);
//						System.out.println(words[i]);
//						System.out.println("From " + r.getFrom() + "  to " + r.getPrev() + " when prev is "+r.getPrev());
//					}
//				}
//			}
//		}
//	}
	

	/**
	 * Applies the rules to the given statement and compares it with
	 * correct tags to obtain the error
	 * 
	 * @param inO : Scanner used to read the given sentence
	 * @param inG : Scanner used to read the given tagged sentence
	 * @param mostProb : map of each word with most probable tags
	 * @param pq : Priority queue of rules
	 * @return : error obtained
	 * @throws IOException : Thrown when unable to open file or file doesn't exists
	 */
	private static double checkError(Scanner inO, Scanner inG, HashMap<String, String> mostProb,
			PriorityQueue<Rule> pq) throws IOException {
		double error=0;
		int total=0;
		ArrayList<LinkedHashMap<String,String>> list= new ArrayList<LinkedHashMap<String,String>>();
		ArrayList<String> finalList= new ArrayList<String>();
		ArrayList<String> Gold= new ArrayList<String>();
		LinkedHashMap<String,String> h = null;

		while(inO.hasNext()){
			h= new LinkedHashMap<String,String>();
			String sentence = inO.nextLine();
			String[] words=sentence.split("[\n\t ]");
			for(String w: words){
				h.put(w,mostProb.get(w));
				total++;
			}
			list.add(h);
		}
		
		while(inG.hasNext()){
			Gold.add(inG.nextLine());
		}
		
		for(LinkedHashMap<String,String> hm:list){
			for(String t:hm.keySet()){
				finalList.add(t+"_"+hm.get(t));
			}
		}
		
		int i=0;
		for(String sentence:Gold){
			String[] words=sentence.split("[\n\t ]");
			for(String w: words){
				if(i>=words.length-1){
					break;
				}
				else{
				if(!w.equals(finalList.get(i))){
					error++;
				}
				i++;
				}
			}
		}
		error/=total;
		error*=100;
		String str= "Without Rules, Error : "+ error +"\n";
		writer.write(str);
		
		
		for(Rule r:pq){
			for(LinkedHashMap<String,String> hm:list){
				i=0;
				String prev = null;
				for(String t:hm.keySet()){
					if(i==0){
						i++;
						prev=hm.get(t);
						continue;
					}
					if(t.equals(r.getFrom()) && prev==hm.get(t)){
						hm.put(t,r.getTo());
					}
					prev=hm.get(t);
				}
			}
		}
		
		finalList= new ArrayList<String>();
		for(LinkedHashMap<String,String> hm:list){
			for(String t:hm.keySet()){
				finalList.add(t+"_"+hm.get(t));
			}
		}
		
		error=0;
		i=0;
		for(String sentence:Gold){
			String[] words=sentence.split("[\n\t ]");
			for(String w: words){
				if(i>=words.length-1){
					break;
				}
				else{
				if(!finalList.get(i).equals(w)){
					error++;
				}
				i++;
				}
			}
		}
		error/=total;
		error*=100;
		
		
		return error;
	}

	/**
	 * Get the best set of rules for the corpus
	 * 
	 * @param corpus : sentences of the corpus
	 * @param mostProb : Map of words with their most probable tag
	 */
	private static void getBestInstance(ArrayList<String> corpus, HashMap<String, String> mostProb) {
		rules=new HashMap<Rule,Integer>();
		HashMap<String,Integer> goodInstance;
		HashMap<String,Integer> badInstance;
		int best;
		String bestP = null;
		for(String fromTag:tags){
			for(String toTag:tags){
				goodInstance= new HashMap<String,Integer>();
				badInstance= new HashMap<String,Integer>();
				for(String sentence:corpus){
					String[] words= sentence.split("[\n\t ]");
					for(int i=1;i<words.length;i++){
						String[] tok= words[i].split("_");
						if(tok[1].equals(toTag) && mostProb.get(tok[0]).equals(fromTag)){
							String prev= words[i-1].split("_")[1];
							change(goodInstance,prev,1);
						}
						else if(tok[1].equals(fromTag) && mostProb.get(tok[0]).equals(fromTag)){
							String prev= words[i-1].split("_")[1];
							change(badInstance,prev,1);
						}
					}
				}
				
				best=0;
				//finding best prev
				for(String prev:goodInstance.keySet()){
					int val=goodInstance.get(prev)- badInstance.getOrDefault(prev, 0);
					if(val>best){
						best=val;
						bestP=prev;
					}
				}
				
				if(best>0){
					
					rules.put(new Rule(fromTag,toTag,bestP), best);
				}
			}
		}
		
	}
	
	/**
	 * Changes the instance value
	 * 
	 * @param instance : map of tag and (good/bad) count instance
	 * @param prev : previous tag
	 * @param i : the change to be made to the count
	 */
	private static void change(HashMap<String, Integer> instance, String prev, int i) {
		int count=instance.getOrDefault(prev, 0)+i;
		instance.put(prev, count);
		
	}

	/**
	 * Reads all the tags into a a list
	 * @param tag : Scanner used to read all the tags
	 */
	private static void readTags(Scanner tag){
		tags= new ArrayList<String>();
		while(tag.hasNext()){
			String t= tag.nextLine();
			tags.add(t);
		}
	}

	/**
	 * Reads each line in the corpus as a separate sentence in the array list
	 * @param in : Scanner used to read the corpus
	 * @return : Array list of corpus sentences
	 */
	private static ArrayList<String> getText(Scanner in) {
		ArrayList<String> text= new ArrayList<String>();
		while(in.hasNext()){
			text.add(in.nextLine());
		}
		return text;
		
	}

	/*
	 * Comparator used for Priority Queue
	 */
	static class MaxComparator implements Comparator<Rule>{

		@Override
		public int compare(Rule arg0, Rule arg1) {
			
			return rules.get(arg1)-rules.get(arg0) ;
		}
	}
	
}
