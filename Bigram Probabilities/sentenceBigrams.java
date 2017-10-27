import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Calculates the bigram probabilities and total of a given sentence for 
 * three scenarios - Without Smoothing, With Add-One Smoothing and With 
 * Good-Turing Discounting
 * 
 * @author Shreya Vishwanath Rao
 * @version 1.0: 2017/09/30 
 *
 */
public class sentenceBigrams {

		static HashMap<String, Integer> corpusBigramCount; //corpus bigrams Without smoothing
		static HashMap<String, Integer> corpusUnigramCount; //corpus unigrams without smoothing
		static HashMap<String, Double> corpusBigramProb;//corpus bigrams prob without smoothing
		
		static HashMap<String, Double> bigramCountS;//corpus count with add-one based on sentence
		static HashMap<String,Double> bigramProbS;//corpus prob with smoothing
		
		static HashMap<Integer, Double> bucketCountT;//corpus bigram bucket count
		static HashMap<String,Double> bigramProbT;//corpus bigram prob with good Turing
		
		static HashMap<String, Integer> bigramCountNoS; //sentence count without smoothing
		static HashMap<String, Double> bigramProbNoS;//sentence bigram prob without smoothing
		
		static HashMap<String,Double> sentenceBigramProbS;//sentence bigram prob with smoothing
		static HashMap<String,Double> sentenceBigramProbT;//sentence bigram prob with good turing
		
		static ArrayList<String> corpusTokens; //corpus tokens
		static ArrayList<String> sentenceTokens; //given sentence tokens
		
		static int corpusNumOfBigrams;
		static int corpusNumOfBigramsU;
		static int corpusNumOfUnigrams;
		static int corpusNumOfTokens;
		static int bucketCount;
		
		static int sentenceNumOfBigrams;
		
		static FileWriter writer;
		
	/**
	 * Main function. Determines the probabilities of the given sentence
	 * based on previously calculated probabilities of the corpus.
	 * Determines the total proability of the given sentence based on the
	 * three scenarios
	 * 
	 * @param args : command line arguments
	 * @throws IOException : throws an error if unable to open a file or access it
	 */
	public static void main(String[] args) throws IOException{
		String filename=null;
		String infile1= "CorpusOutputNoS.txt";
		String infile2= "CorpusOutputS.txt";
		String infile3= "CorpusOutputT.txt";
		String outfile="SentenceOutput.txt";
		String str;
		
		File out= new File(outfile);
		writer= new FileWriter(out);
		
		String given=null; 
		if(args.length>0)
			filename=args[0];
		
		corpusTokens = new ArrayList<String>();
		corpusUnigramCount = new HashMap<String, Integer>();
		corpusBigramCount = new HashMap<String, Integer>();
		corpusBigramProb= new HashMap<String,Double>();
		bigramProbS= new HashMap<String,Double>();
		bigramProbT= new HashMap<String,Double>();
		bucketCountT= new HashMap<Integer, Double>();
		corpusNumOfBigrams=0;
		
		Scanner in = new Scanner(new File(filename));
		Scanner corpus1 = new Scanner(new File(infile1));
		Scanner corpus2 = new Scanner(new File(infile2));
		Scanner corpus3 = new Scanner(new File(infile3));
		
		readCorpusFile(corpus1, corpus2, corpus3);
		
//-------------------------Sentence No Smoothing---------------------------------
		str="WITHOUT SMOOTHING\n\n";
		writer.write(str);
		
		//find the unigram, Bigram and bigram prev count of the Given sentence
		sentenceNumOfBigrams=findSentenceBigramCountNoS(in);
		
		//find Sentence Bigram Prob
		findSentenceBigramProbNoS();
		
//--------------------------No Smoothing ends----------------------------
		
//-----------------------------Add-one Smoothing begins-----------------
		str="WITH ADD_ONE SMOOTHING\n";
		writer.write(str);
		
		findBigramProbSmoothing(bigramCountNoS,corpusNumOfUnigrams);
		
//-----------------------------Add-one Smoothing Ends-------------------
		
//------------------------------Good Turing begins----------------------

		str="WITH GOOD TURING DISCOUNTING\n";
		writer.write(str);
		
		//finding bigram probabilities with Good Turing discounting
		findBigramProbTuring();
		
		
//------------------------------Good Turing ends----------------------
		
		writer.close();
	}
	

	/**
	 * Reads three files to obtain the unigram/bigram count and probabilities
	 * of the corpus 
	 * 
	 * @param in1 : Scanner to read file containing data when no smoothing
	 * @param in2 : Scanner to read file containing data with Add-One smoothing
	 * @param in3 : Scanner to read file containing data with  Good Turing discounting
	 */
	public static void readCorpusFile(Scanner in1, Scanner in2, Scanner in3){
		
		if(in1.hasNext())
			corpusNumOfBigrams=Integer.parseInt(in1.nextLine());
		
		if(in1.hasNext())
			corpusNumOfBigramsU=Integer.parseInt(in1.nextLine());
		
		if(in1.hasNext())
			corpusNumOfUnigrams=Integer.parseInt(in1.nextLine());
		
		if(in1.hasNext())
			corpusNumOfTokens=Integer.parseInt(in1.nextLine());
			
		if(in1.hasNext())
			in1.nextLine();
		
		//reads the corpus Unigram count
		for(int i=0;i<corpusNumOfUnigrams;i++){
			if(in1.hasNext()){
				String str=in1.nextLine();
				String[] word=str.split(" ");
				corpusUnigramCount.put(word[0], Integer.parseInt(word[1]));
			}
		}
		
		if(in1.hasNext())
			in1.nextLine();
		
		//reads the corpus Bigram Count
		for(int i=0;i<corpusNumOfBigramsU;i++){
			if(in1.hasNext()){
				String str=in1.nextLine();
				String[] word=str.split(" ");
				corpusBigramCount.put(word[0]+ " "+word[1], Integer.parseInt(word[2]));
			}
		}
		
		if(in1.hasNext())
			in1.nextLine();
		
		//reads the corpus Bigram Prob
		for(int i=0;i<corpusNumOfBigramsU;i++){
			if(in1.hasNext()){
				String str=in1.nextLine();
				String[] word=str.split(" ");
				corpusBigramProb.put(word[0]+ " "+word[1], Double.parseDouble(word[2]));
			}
		}
		
		//reads the corpus Bigram Prob with smoothing
		for(int i=0;i<corpusNumOfBigramsU;i++){
			if(in2.hasNext()){
				String str=in2.nextLine();
				String[] word=str.split(" ");
				bigramProbS.put(word[0]+ " "+word[1], Double.parseDouble(word[2]));
			}
		}
		
		if(in3.hasNext())
			bucketCount=Integer.parseInt(in3.nextLine());
		
		if(in3.hasNext())
			in3.nextLine();
		
		//reads the corpus Bigram Prob with good turing
		for(int i=0;i<bucketCount;i++){
			if(in3.hasNext()){
				String str=in3.nextLine();
				String[] word=str.split(" ");
				
				bucketCountT.put(Integer.parseInt(word[0]), Double.parseDouble(word[1]));
			}
		}

		if(in3.hasNext())
			in3.nextLine();
		
		//reads the corpus Bigram Prob with good turing
		for(int i=0;i<corpusNumOfBigramsU;i++){
			if(in3.hasNext()){
				String str=in3.nextLine();
				String[] word=str.split(" ");
				bigramProbT.put(word[0]+ " "+word[1], Double.parseDouble(word[2]));
			}
		}
	}
	
	/**
	 * Find Sentence unigram Bigram Count based on sentence and corpus without smoothing
	 * 
	 * @param in : Scanner to read the given sentence
	 * @return : returns the number of bigrams in the sentence
	 */
	public static int findSentenceBigramCountNoS(Scanner in){
		int countB,countU;
		sentenceTokens = new ArrayList<String>();
		bigramCountNoS = new HashMap<String, Integer>();
		
		//Find sentence tokens
		while(in.hasNext()){
			String sentence= in.nextLine();
			
			String[] word= sentence.split("[\n\t ]");
			for(String s:word){
				sentenceTokens.add(s);
			}
		}
		
		//Find unigram,bigram and bigram Prev count
		int numOfTokens=sentenceTokens.size();
		int sentenceNumOfBigrams=0;
		for(int i=0;i<numOfTokens-1;i++){
			
			//bigram count
			String seq=sentenceTokens.get(i)+ " " + sentenceTokens.get(i+1);
			
			if(!bigramCountNoS.containsKey(seq)){
			//bigram Prev count
				countB=corpusBigramCount.getOrDefault(seq, 0);
				bigramCountNoS.put(seq, countB);
			}
			sentenceNumOfBigrams++;
		}
		return sentenceNumOfBigrams;
	}

	
	/**
	 * finds the bigram probabilities without smoothing
	 * @throws IOException : throws an exception when unable to open or access the file 
	 */
	public static void findSentenceBigramProbNoS() throws IOException{
		bigramProbNoS = new HashMap<String, Double>();
		double total=1;
		
		String str="BIGRAM\t\tCOUNT\tPROB WITHOUT SMOOTHING\n";
		for(String s: bigramCountNoS.keySet()){
			double prob=corpusBigramProb.getOrDefault(s, (double) 0);
			str+=s+" \t\t"+ String.valueOf(bigramCountNoS.get(s))+ "\t\t"+ String.valueOf(prob)+"\n";
			bigramProbNoS.put(s, prob);
			total*=prob;
		}
		str+="\n";
		str+="Without Smoothing, Probability is "+ String.valueOf(total)+"\n\n";
		writer.write(str);
	}

	
	/**
	 * Find Bigram Probabilities of sentence with Add-one smoothing
	 * 
	 * @param bigramCountNoS : bigram counts of the corpus when no smoothing is done
	 * @param v : total number of unique tokens in the corpus
	 * @throws IOException : throws an exception when unable to open or access the file
	 */
	private static void findBigramProbSmoothing(HashMap<String, Integer> bigramCountNoS, int v) throws IOException {
		
		sentenceBigramProbS= new HashMap<String,Double>();
		double total=1;
		
		String str="BIGRAM\t\tCOUNT\tPROB WITH ADD-ONE\n";
		
		for(String s:bigramCountNoS.keySet()){
			if(bigramProbS.containsKey(s)){
				double prob=bigramProbS.get(s);
				sentenceBigramProbS.put(s, prob);
				str+=s+" \t\t"+ String.valueOf(bigramCountNoS.get(s))+ "\t\t"+String.valueOf(prob)+"\n";
				total*=prob;
			}
			else{
				String[] word=s.split(" ");
					
				int count=bigramCountNoS.get(s);
				double prob= (double)(count+1)/(double)(corpusUnigramCount.getOrDefault(word[0],0)+ v);
				sentenceBigramProbS.put(s,prob);
				total*=prob;
				str+=s+" \t\t"+ String.valueOf(bigramCountNoS.get(s))+ "\t\t"+String.valueOf(prob)+"\n";
			}
		}
		str+="\nWith Add-One, Probability is "+ String.valueOf(total)+"\n\n";
			
		writer.write(str);
	}
			
	/**
	 * Find Bigram Probabilities of sentence with Good Turing Discounting
	 * @throws IOException : throws an exception when unable to open or access the file
	 */
	private static void findBigramProbTuring() throws IOException {
				
		bigramProbT= new HashMap<String, Double>();
		double prob;
		double total=1;
		String str="BIGRAM\t\tCOUNT\tPROB WITH ADD-ONE\n";
				
		for(String s: bigramCountNoS.keySet()){
			if(bigramProbT.containsKey(s)){
				prob=bigramProbT.get(s);
			}
			else{
				int count=bigramCountNoS.get(s);
				if(count==0){
					prob= bucketCountT.getOrDefault(1,0.0)/corpusNumOfBigrams;
				}
				else{
					double bucket= ((double)(count+1)*bucketCountT.getOrDefault(count+1,0.0))/bucketCountT.get(count);
					prob= bucket/corpusNumOfBigrams;
				}
			}
			total*=prob;
			str+=s+" \t\t"+ String.valueOf(bigramCountNoS.get(s))+ "\t\t"+String.valueOf(prob)+"\n";
			bigramProbT.put(s, prob);
		}
		str+="\n With Good Turing, Probability is "+ String.valueOf(total)+ "\n\n";
		writer.write(str);
			
	}
	
}
