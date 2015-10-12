package crawl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SimilarityMain {
	/**
	 * @param args
	 */
	public static final String ignorechar="[.,!':+-\\/*{}1234567890;:%=`~]";
	public static final ArrayList<String> stopwords = new ArrayList<String>();
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub		
		BufferedReader br = new BufferedReader(new FileReader("/home/hari/stop1.txt"));
		String currentLine;
		while((currentLine=br.readLine())!=null){
			stopwords.add(currentLine);
		}

		String sent="agriculture";

		Similarity ls=new Similarity(stopwords,ignorechar, 0);
		ls.parseFile("/home/hari/doc1");
		ls.compute(sent);
		ls.topBiGrams();
		ls.tfIdfCalculator();
		System.out.println(ls.getCosineSimilarity());
	}
}
