package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		System.out.println(keywordsIndex);
	}

	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		HashMap<String, Occurrence> table= new HashMap<String, Occurrence>(100, 2.0f); 
		Scanner scan = new Scanner(new File(docFile));
		
		while(scan.hasNext()){
			
			String word = getKeyWord(scan.next());
			
			if(word != null){
				
				if(table.containsKey(word)== false){
					Occurrence occur = new Occurrence(docFile, 1);
					table.put(word, occur);
				}
				else{
					if(table .get(word).document.equals(docFile)){
						table .get(word).frequency++;
					}
			}
		}
	}
	
		return table ;
		}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		Iterator<String> it = kws.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			Occurrence occurence = kws.get(key);
			
			if(keywordsIndex.containsKey(key)){
				keywordsIndex.get(key).add(occurence);
				ArrayList<Occurrence> list = keywordsIndex.get(key);
				insertLastOccurrence(list);
				
			}
			else{
			ArrayList<Occurrence> list = new ArrayList<Occurrence>();
			list.add(occurence);
			keywordsIndex.put(key, list);
			}
			
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		
		String nWord = punctuation(word);
		if(nWord == null)
		return null;
		else{
			if(noiseWords.containsValue(nWord) == true)
				return null;
			return nWord;
		}
	}
	
	private String punctuation(String word){
		String nWord = word.toLowerCase();
		for(int i = nWord.length()-1; i > 0; i--){
			if(nWord.charAt(i) < 'a' || nWord.charAt(i) > 'z'){
				nWord = nWord.substring(0, nWord.length()-1);
			}
			else
				break;
		}	
		int j = 0;
		while(j != nWord.length()){
			if(nWord.charAt(j) < 'a' || nWord.charAt(j) > 'z'){
				return null;
			}
			j++;
		}
		
		return nWord;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		//Binary Search
		ArrayList<Integer> midl = new ArrayList<Integer>();
		Occurrence occur = occs.get(occs.size()-1);
		int item = occur.frequency;
		int mid=0;
		int lo = 0, hi = occs.size()-2;
		while(lo <= hi){
		mid = (lo + hi)/2;
		midl.add(mid);
		if(occs.get(mid).frequency == item){
			occs.remove(occur);
			occs.add(mid+1, occur);
			return midl;
			}
		else if(occs.get(mid).frequency < item){
			hi = mid - 1;
			
		}
		else if(occs.get(mid).frequency > item){
			lo = mid + 1;
		}
		}
		occs.remove(occur);
		occs.add(hi+1, occur);
		
		return midl;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		Iterator<String> it = keywordsIndex.keySet().iterator();
		ArrayList<Occurrence> l1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> l2 = new ArrayList<Occurrence>();
		int count = 0;
		while(count != 2){
			String n = it.next();
			if(n.equals(kw1)){
				l1 = keywordsIndex.get(n);
				count++;
			}
			if(n.equals(kw2)){
				l2 = keywordsIndex.get(n);
				count++;
			}
		}
		
		
		ArrayList<String> fin= new ArrayList<String>();
		int i = 0, j = 0;
		while(i < l1.size() && j < l2.size()){
			
				if(fin.size() == 5)
					break;
				if(l1.get(i).frequency < l2.get(j).frequency){
					if(!fin.contains(l2.get(j).document))
						fin.add(l2.get(j).document);
					j++;
					
				}
				else if(l1.get(i).frequency > l2.get(j).frequency){
					if(!fin.contains(l1.get(i).document))
						fin.add(l1.get(i).document);
					i++;
				}
				else{
					if(l1.get(i).document.equals(l2.get(j).document) && !fin.contains(l1.get(i).document)){
						fin.add(l1.get(i).document);
						i++;
						j++;
					}
					else if(!fin.contains(l1.get(i).document)){
						fin.add(l1.get(i).document);
						i++;
					}
					else if(!fin.contains(l2.get(j).document)){
						fin.add(l2.get(j).document);
						j++;
						i++;
					}
					else{
						i++;
						j++;
					}
				}
		}
	// loop done
		
	if(fin.size() < 5){
		if(i < l1.size()){
			while(i < l1.size() && fin.size() < 5){
				if(!fin.contains(l1.get(i).document)){
					fin.add(l1.get(i).document);
				}
				i++;
			}
		}
		if(j < l2.size() && fin.size() < 5){
			while(j < l2.size()){
				if(!fin.contains(l2.get(j).document)){
					fin.add(l2.get(j).document);
				}
				j++;
			}
		}
	}
		
	
	return fin;
	}
}