package search;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class searchDriver {

public static void main(String[] args)
		throws FileNotFoundException{
		LittleSearchEngine search = new LittleSearchEngine();
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Enter a file name");
		String docsFile = scan.nextLine();
		System.out.println("Enter Noise words file: ");
		String noiseWordsFile = scan.nextLine();
		search.makeIndex(docsFile, noiseWordsFile);
		search.loadKeyWords(docsFile);
		System.out.println("enter a word to search for");
		String kw1 = scan.nextLine();
		System.out.println("enter a word to search for");
		String kw2 = scan.nextLine();
		System.out.println("1: " + kw1 + " 2: " + kw2);
		System.out.println(search.top5search(kw1, kw2));
		
	}
}