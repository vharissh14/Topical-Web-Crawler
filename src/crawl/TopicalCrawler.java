package crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TopicalCrawler {

	Queue<String> url_QUEUE = new LinkedList<String>();
	
	public static final String ignorechar="[.,!':+-\\/*{}1234567890;:%=`~]";
	public static final ArrayList<String> stopwords = new ArrayList<String>();
	public static final ArrayList<String> relv_url = new ArrayList<String>();
	public static final Similarity ls=new Similarity(stopwords,ignorechar, 0);
	
	public double similarity_measure(String page) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("/home/hari/stop1.txt"));
		String currentLine;
		while((currentLine=br.readLine())!=null){
			stopwords.add(currentLine);
		}
		Similarity ls=new Similarity(stopwords,ignorechar, 0);
		ls.parseFile("/home/hari/doc1");
		ls.compute(page);
		ls.topBiGrams();
		ls.tfIdfCalculator();
		return ls.getCosineSimilarity();
	}
	
	
	public void crawl() throws IOException{
		for(String url: url_QUEUE){
			String url1 = url_QUEUE.poll();
			Document doc = Jsoup.connect(url1).timeout(0).ignoreContentType(true).ignoreHttpErrors(true).get();
			String text = doc.text();
			if(similarity_measure(text)>1.0){
				relv_url.add(url1);
			}
			Elements links = doc.select("a[href]");
			Elements links1 = links.not("a[href~=(?i)\\.(png|jpe?g)]");
			Queue<String[]> temp_queue = new LinkedList<String[]>();
			for(Element link: links1){
					String[] url_coll=new String[3];
					url_coll[0] = link.attr("abs:href");
					url_coll[1] = link.text();
					String link_context=" ";
					Element parent=link.parent();
					while (parent!=null&&parent.text()==null&&!parent.text().matches("^"))
					{
						parent=parent.parent();
					}
					if (parent!=null)
						link_context=parent.text();
					link_context.trim();
					url_coll[2]=link_context;
					temp_queue.add(url_coll);
			}
			double val;
			for(String[] tmp_url: temp_queue){
				if((val=similarity_measure(tmp_url[1].replaceAll("[\\[\\]]", "")))>0.15){
					System.out.println(tmp_url[1]);
					url_QUEUE.add(tmp_url[0]);
					//temp_queue.poll();
					if (temp_queue.isEmpty())
					{
						temp_queue.remove();
					}
				}
				else{
					if ((val=similarity_measure(tmp_url[2].replaceAll("[\\[\\]]", "")))>0.15)
					{
						System.out.println(tmp_url[1]);
						url_QUEUE.add(tmp_url[0]);
						//temp_queue.poll();
						if (temp_queue.isEmpty())
						{
							temp_queue.remove();
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		TopicalCrawler t = new TopicalCrawler();
		t.url_QUEUE.add("https://en.wikipedia.org/wiki/Agriculture");
		t.crawl();
	}
}
