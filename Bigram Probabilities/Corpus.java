import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Calculates the Bigram Counts and Probabilities of the given corpus for
 * three scenarios - Without Smoothing, With Add-One Smoothing and With 
 * Good-Turing Discounting
 * 
 * @author Shreya Vishwanath Rao
 * @version 1.0: 2017/09/30 
 *
 */

public class Corpus {
	
	static HashMap<String, Integer> corpusBigramCount; //Without smoothing
	static HashMap<String, Integer> corpusUnigramCount; //without smoothing
	static HashMap<String, Double> corpusBigramProb;//without smoothing
	
	static HashMap<String, Double> bigramCountS;//with add-one based on sentence
	static HashMap<String,Double> bigramProbS;//prob with smoothing
	
	static HashMap<Integer, Double> bucketCountT;//bucket count
	static HashMap<String,Double> bigramProbT;//prob with smoothing
	static HashMap<Integer, Double> bigramCountTI;//bucket count
	
	static ArrayList<String> corpusTokens; //corpus tokens
	
	static int corpusNumOfBigrams;
	
	static FileWriter writer1;
	static FileWriter writer2;
	static FileWriter writer3;
	

	/**
	 * Main function. Takes a corpus as input to calculate the Bigram Counts
	 * and probabilities for the three scenarios.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException{
		String filename=null;
		String outfileNoS="CorpusOutputNoS.txt";
		String outfileS="CorpusOutputS.txt";
		String outfileT="CorpusOutputT.txt";
		String str;
		
		File out1= new File(outfileNoS);
		writer1= new FileWriter(out1);
		
		File out2= new File(outfileS);
		writer2= new FileWriter(out2);
		
		File out3= new File(outfileT);
		writer3= new FileWriter(out3);
		
		String given=null; 
		if(args.length>0)
			filename=args[0];
		
		corpusTokens = new ArrayList<String>();
		corpusUnigramCount = new HashMap<String, Integer>();
		corpusBigramCount = new HashMap<String, Integer>();
		corpusBigramProb= new HashMap<String,Double>();
		corpusNumOfBigrams=0;
		
		Scanner in = new Scanner(new File(filename));
		
//----------------------------------CORPUS BEGIN-------------------------
		//finds unigram and Bigram count in Corpus and display it
		corpusNumOfBigrams=findBigramCount(in,corpusTokens,corpusUnigramCount,corpusBigramCount);
		
		//Find corpus Bigram Prob and display it
		findBigramProb(corpusUnigramCount,corpusBigramCount,corpusBigramProb,corpusTokens,corpusNumOfBigrams);
		
		int V= corpusUnigramCount.size();

		//display details of corpus
		str=String.valueOf(corpusNumOfBigrams)+"\n"; //number of Bigrams
		str+=String.valueOf(corpusBigramCount.size())+"\n";//Unique Bigram count 
		str+=String.valueOf(V)+"\n";//Unique Unigram count 
		str+= String.valueOf(corpusTokens.size())+"\n";//Total count
		str+="\n";
		writer1.write(str);
		
		displayCount1(corpusUnigramCount);
		displayCount1(corpusBigramCount);
		displayProb1(corpusBigramProb);
		
		
//-----------------------------------CORPUS END--------------------------------

//-------------------------Add-one Smoothing begins--------------------
		
		findBigramProbSmoothing(corpusBigramCount,V);
		displayProb2(bigramProbS);
//----------------------Add-one smoothing ends--------------------------
		
//-------------------Good-Turing Discounting Begins-------------------------
		
		//finds the initial bucket count using the bigram count before smoothing 
		doBucketCount(corpusBigramCount);
		
		str=bucketCountT.size()+"\n\n";
		writer3.write(str);
		displayBucketCount3();
		
		//finding new Counts with Good Turing discounting
		findBigramCountsTuring();
		
		
		//finding bigram probabilities with Good Turing discounting
		findBigramProbTuring();
		displayProb3(bigramProbT);
		
//--------------------Good-Turing Discounting Ends-------------------------
		writer1.close();
		writer2.close();
		writer3.close();
	}
	
	/**
	 * Writes the contents of the bucket count into the file
	 * @throws IOException : throws an error if unable to write into file
	 */
	private static void displayBucketCount3() throws IOException {
		String str="";
		
		//bucket count
		for(int i:bucketCountT.keySet()){
			str+=String.valueOf(i)+ " " + String.valueOf(bucketCountT.get(i))+"\n";
		}
		str+="\n";
		writer3.write(str);
		
	}

	/**
	 * Finds the bigram counts in the given corpus
	 * 
	 * @param in : Scanner that reads the contents of the corpus
	 * @param tokens : list of all token in the corpus
	 * @param unigramCount : unigram token counts in the corpus
	 * @param bigramCount : bigram counts in the corpus
	 * @return : total number of bigrams in the corpus
	 */
	public static int findBigramCount(Scanner in, ArrayList<String> tokens, HashMap<String, Integer> unigramCount, HashMap<String, Integer> bigramCount){
		int countU, countB;
		int numOfBigrams=0;
		
		while(in.hasNext()){
			String sentence= in.nextLine();
			String[] t=sentence.split("[\n\t ]");
			for(int i=0;i<t.length;i++){
				tokens.add(t[i]);
			}
		}
		
		int numOfTokens=tokens.size();
		for(int i=0;i<numOfTokens-1;i++){
			//unigramCount
			countU=unigramCount.getOrDefault(tokens.get(i),0);
			unigramCount.put(tokens.get(i), countU+1);
			
			//bigram count
			String seq=tokens.get(i)+ " " + tokens.get(i+1);
			countB=bigramCount.getOrDefault(seq, 0);
			bigramCount.put(seq, countB+1);
			numOfBigrams++;
		}
		
		//for the last token
		countU=unigramCount.getOrDefault(tokens.get(numOfTokens-1),0);
		unigramCount.put(tokens.get(numOfTokens-1), countU+1);
		
		return numOfBigrams;
	}
	
	/**
	 * Calculates the bigram probabilities in the corpus
	 * 
	 * @param unigramCount : previously calculated unigram counts of the corpus
	 * @param bigramCount : previously calculated bigram counts of the corpus
	 * @param bigramProb :bigram probabilities of the corpus
	 * @param tokens : all the tokens in the corpus 
	 * @param numOfBigrams : total number of bigrams in the corpus
	 */
	public static void findBigramProb(HashMap<String, Integer> unigramCount, HashMap<String, Integer> bigramCount, HashMap<String, Double> bigramProb, ArrayList<String> tokens, int numOfBigrams){
		
		for(String seq : bigramCount.keySet()){
			String[] words=seq.split(" ");
			double probDenom=(double)unigramCount.get(words[0])/(double)tokens.size();
			double prob= ((double)bigramCount.get(seq)/(double)numOfBigrams)/probDenom;
			bigramProb.put(seq, prob);
		}
	}

	/**
	 * Writes the bigram counts into the file
	 * 
	 * @param bigramCount : bigram counts calculated
	 * @throws IOException : throws an error if unable to write into file
	 */
	public static void displayCount1(HashMap<String, Integer> bigramCount) throws IOException{
		String str="";

		//display bigram Counts
		for(String s:bigramCount.keySet()){
			str+=s + " "+ String.valueOf(bigramCount.get(s))+"\n";
		}
		str+="\n";
		writer1.write(str);
	}
	
	/**
	 * Writes the bigram probabilities into the file
	 * 
	 * @param bigramCount : bigram probabilities calculated
	 * @throws IOException : throws an error if unable to write into file
	 */
	public static void displayProb1(HashMap<String, Double> bigramProb) throws IOException{
		String str="";
		for(String s:bigramProb.keySet()){
			str+=s + " "+ String.valueOf(bigramProb.get(s))+"\n";
		}
		writer1.write(str);
	}
	
	/**
	 * Writes the bigram probabilities into the file
	 * 
	 * @param bigramCount : bigram probabilities calculated
	 * @throws IOException : throws an error if unable to write into file
	 */
	public static void displayProb2(HashMap<String, Double> bigramProb) throws IOException{
		String str="";
		for(String s:bigramProb.keySet()){
			str+=s + " "+ String.valueOf(bigramProb.get(s))+"\n";
		}
		writer2.write(str);
	}
	
	/**
	 * Writes the bigram probabilities into the file
	 * 
	 * @param bigramCount : bigram probabilities calculated
	 * @throws IOException : throws an error if unable to write into file
	 */
	public static void displayProb3(HashMap<String, Double> bigramProb) throws IOException{
		String str="";
		for(String s:bigramProb.keySet()){
			str+=s + " "+ String.valueOf(bigramProb.get(s))+"\n";
		}
		writer3.write(str);
	}
			
	/**
	 * Find Bigram Probabiliteis of sentence with Add-one smoothing
	 * 
	 * @param bigramCount : previously caluculated bigram counts of the corpus
	 * @param v : uniques number of tokens in the corpus
	 */
	private static void findBigramProbSmoothing(HashMap<String, Integer> bigramCount, int v) {

		bigramProbS= new HashMap<String,Double>();
		
		for(String s:bigramCount.keySet()){
			String[] word=s.split(" ");
			
			int count=bigramCount.get(s);
			double prob= (double)(count+1)/(double)(corpusUnigramCount.getOrDefault(word[0],0)+ v);
			bigramProbS.put(s,prob);
		}
	
	}
			
	/**
	 * Bucket Count for Good turing discounting
	 * 
	 * @param bigramCount : previously caluculated bigram counts of the corpus
	 */
	private static void doBucketCount(HashMap<String, Integer> bigramCount) {
		//bucketCountT
		bucketCountT= new HashMap<Integer,Double>();
				
		for(String s:bigramCount.keySet()){
			double count= bucketCountT.getOrDefault(bigramCount.get(s), (double)0);
			bucketCountT.put(bigramCount.get(s), count+1);
		}
	}
			
	/**
	 * finding new counts using Good turing discounting
	 */
	private static void findBigramCountsTuring() {

		bigramCountTI= new HashMap<Integer,Double>();
		double value;
		for(int i:bucketCountT.keySet()){
			if(i==0)
				continue;
			value= ((i+1)*bucketCountT.getOrDefault(i+1, 0.0))/bucketCountT.get(i);
			bigramCountTI.put(i,value);
		}
	}
			
	/**
	 * Calculates Good Turing probabilities
	 */
	private static void findBigramProbTuring() {
				
		bigramProbT= new HashMap<String, Double>();
		double prob;
		
		for(String s: corpusBigramCount.keySet()){
			int count=corpusBigramCount.get(s);
			if(count==0){
				prob= bucketCountT.getOrDefault(1,0.0)/corpusNumOfBigrams;
			}
			else
				prob= bigramCountTI.get(count)/corpusNumOfBigrams;
			bigramProbT.put(s, prob);
		}
			
	}
}
