import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Creates a unigram model containing the most probable POS tag for each
 * word in the provided corpus
 * 
 * @author Shreya Vishwanath Rao
 * @version 1.0: 2017/09/30
 *
 */

public class POSTagging {
	
	static HashMap<String, Word> wordList; 
	static HashMap<String, String> mostProbTag;
	
	 * Finds the most probable tag for each sentence in the corpus
	 * 
	 * @param corpus :list of all words
	 * @return : Map of words with the most probable tag
	 */
	public static HashMap<String, String> mostProb(ArrayList<String> corpus){
		Word tempWord;

		wordList = new HashMap<String, Word>();
		for(String sentence:corpus){
			String[] words=sentence.split("[\n\t ]");
			for(String s:words){
				String[] wordTag= s.split("_");
				if(wordList.containsKey(wordTag[0])){
					tempWord=wordList.get(wordTag[0]);
					tempWord.putTaggedCount(wordTag[1]);
				}
				else{
					tempWord=new Word(wordTag[0]);
					tempWord.putTaggedCount(wordTag[1]);
				}
				wordList.put(wordTag[0], tempWord);
			}
		}
		
		mostProbTag = new HashMap<String, String>();
		for(String s:wordList.keySet()){
			Word w=wordList.get(s);
			String tag= w.getMostProb();
			mostProbTag.put(s, tag);
		}
		return mostProbTag;
	}
}
