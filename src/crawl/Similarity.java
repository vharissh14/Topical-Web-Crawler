package crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;


class MyComparator implements Comparator {

	/**
	 *	Comparator for Sorting the Map by value
	 */
	Map map;
	public MyComparator(Map map) {
		this.map = map;
	}
	public int compare(Object a, Object b) {
		if((Integer)map.get(a) >= (Integer) map.get(b)){
			return -1;
		}
		else { 
			return 1;
		}
	}
}

public class Similarity {

	public ArrayList<String> stopwords;
	public String ignorechar;
	public ArrayList<String> dict;
	public int dcount;
	/**
	 * Map Declarations
	 */
	List<double[]> tfidfDocsVector = new ArrayList<double[]>();
	Map<String,Integer> m2=new TreeMap<String,Integer>();
	Map<String,Integer> newMap;
	Map<String,Integer> biGram = new LinkedHashMap<String, Integer>();
	List<String[]> termsDocsArray = new ArrayList<String[]>();
	Set<String> allTerms = new HashSet<String>(); 

	public Similarity(ArrayList<String> stopwords1, String ignorechar1, int dcount1){
		/**
		 * 	Constructor to initialize parameters
		 */
		this.stopwords = stopwords1;
		this.ignorechar = ignorechar1;
		this.dcount = dcount1;
	}

	public void parseFile(String filepath) throws FileNotFoundException, IOException {
		/**
		 *	stop words removal, ignore char removal, indexing
		 */
		File[] allfiles = new File(filepath).listFiles();
		BufferedReader in = null;
		for (File f : allfiles) {
			if (f.getName().endsWith(".txt")) {
				in = new BufferedReader(new FileReader(f));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = in.readLine()) != null) {
					sb.append(s);
				}
				Reader reader = new StringReader(sb.toString());
				DocumentPreprocessor dp = new DocumentPreprocessor(reader);
				ArrayList<String> sentenceList = new ArrayList<String>();
				for (List<HasWord> sentence : dp) {
					String sentenceString = Sentence.listToString(sentence);
					sentenceList.add(sentenceString.toString());
				}
				String[] token=cooccur_mat(sentenceList);
				termsDocsArray.add(token);
			}
		}

	}

	public void compute(String sent){
		Reader reader = new StringReader(sent);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		ArrayList<String> sentenceList = new ArrayList<String>();
		for (List<HasWord> sentence : dp) {
			String sentenceString = Sentence.listToString(sentence);
			sentenceList.add(sentenceString.toString());
		}
		String token[] = cooccur_mat(sentenceList);
		termsDocsArray.add(token);
	}

	public void tfIdfCalculator() {
		double tf;
		double idf; 
		double tfidf;     
		for (String[] docTermsArray : termsDocsArray) {
			double[] tfidfvectors = new double[allTerms.size()];
			int count = 0;
			for (String terms : allTerms) {
				tf = new TfIdf().tfCalculator(docTermsArray, terms);
				idf = new TfIdf().idfCalculator(termsDocsArray, terms);
				tfidf = tf * idf;
				tfidfvectors[count] = tfidf;
				count++;


			}
			this.dcount+=1;
			tfidfDocsVector.add(tfidfvectors);    
		}
	}

	public ArrayList<Double> getCosineSimilarity() {
		int i=3; int j=0;
		ArrayList<Double> cos_val = new ArrayList<Double>();	
		ArrayList<Double> cos = new ArrayList<Double>();
		while(i!=j && j<3) {
			CosineSimilarity cs = new CosineSimilarity();
			cos_val.add(cs.cosineSimilarity(tfidfDocsVector.get(i), tfidfDocsVector.get(j)));
			j++;
		}
		double b = Collections.max(cos_val);
		for(double d: cos_val){
			double c = d/b;
			cos.add(c);
		}
		return cos;
	}


	public String[] cooccur_mat(ArrayList<String> sent1){
		List<String> words = new ArrayList<String>();
		List<String> docTerms=new ArrayList<String>();
		for (String sent : sent1)
		{
			String term[]=sent.toLowerCase().replaceAll(this.ignorechar, "").split("\\s");
			for (int i=0;i<term.length;i++)
			{	
				if (!this.stopwords.contains(term[i])&&!term[i].matches("^\\s*$"))
				{
					docTerms.add(term[i]);
					allTerms.add(term[i]);
					if(i+1!=term.length){
						if (!this.stopwords.contains(term[i+1])&&!term[i+1].matches("^\\s*$"))
						{
							String temp=term[i]+" "+term[i+1];
							docTerms.add(temp);
							if (!m2.containsKey(temp))
							{
								m2.put(temp, 1);
							}
							else
							{
								int val=m2.get(temp);
								val+=1;
								m2.put(temp, val);
							}
						}
					}
					else{
						allTerms.add(term[i]);
					}
				}
			}

		}
		String[] tokenisedTerms=docTerms.toArray(new String[docTerms.size()]);
		MyComparator comp=new MyComparator(m2);
		newMap = new TreeMap(comp);
		newMap.putAll(m2);
		return tokenisedTerms;
	}

	public void topBiGrams(){
		int count=0;
		for(Map.Entry<String, Integer> s : newMap.entrySet()){
			if(count<50){
				String bigram = s.getKey();
				int count1 = s.getValue();
				biGram.put(bigram, count1);
				count++;
			}
			else{
				break;
			}
		}
		for(Map.Entry<String, Integer> s: biGram.entrySet()){
			allTerms.add(s.getKey());
		}
	}
}