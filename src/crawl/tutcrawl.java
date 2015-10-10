package crawl;




import org.jsoup.Jsoup;
//import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;

import java.io.IOException;

/**
* Example program to list links from a URL.
*/
public class tutcrawl {	
	static int j=0;
	static //int max_d=3;
	String[] q=new String[10000];
	Queue<String> queue=new LinkedList();
	void crawl(String url,int depth,int max_d) throws IOException,InterruptedException
	{
		if (depth>max_d)
		{
			return;
		}

		Document doc = Jsoup.connect(url).timeout(0).ignoreContentType(true).ignoreHttpErrors(true).get();
		Elements links = doc.select("a[href]");
		Elements links1 = links.not("a[href~=(?i)\\.(png|jpe?g)]");
		int i=0;
		for (Element link : links1)
		{
			Element parent=link.parent();
			Element sibling=link.lastElementSibling();
			if (i==12)
			{
				break;
			}
			q[j]=link.attr("abs:href");
			System.out.println("###############################");
			String decode=q[j].replaceAll("[^a-zA-Z0-9]" , " ");
			decode=decode.replaceAll("https|http|en|org"," ");
			decode=decode.trim().replaceAll(" +"," ");
			System.out.println(decode+" ");
			System.out.println("-------------------------------");
			while (parent!=null&&parent.text()==null)
			{
				parent=parent.parent();
			}
			System.out.println(parent.text());
			System.out.println("-------------------------------");
			if (sibling!=null)
				System.out.println(sibling.text());
			System.out.println("-------------------------------");
			System.out.println(link.text());
			System.out.println("###############################");
			queue.add(q[j]);
			i+=1;
			j+=1;
		}
		int k=0;
		while (!queue.isEmpty()&&k<i)
		{
			crawl(queue.remove(),depth+1,max_d);
			k+=1;
		}

	}
	public static void main(String[] args) throws IOException, InterruptedException {
		String url = "https://en.wikipedia.org/wiki/Rice";
		tutcrawl t=new tutcrawl();
		t.crawl(url,1,2);


	}



}


