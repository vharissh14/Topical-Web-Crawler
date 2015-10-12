package crawl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SimilarityMain {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		String url="https://en.wikipedia.org/wiki/Rice";
		Document doc=Jsoup.connect(url).timeout(0).ignoreContentType(true).ignoreHttpErrors(true).get();
		String text=doc.text();
		Elements links = doc.select("a[href]");
		Elements links1 = links.not("a[href~=(?i)\\.(png|jpe?g)]");
		for (Element link : links1)
		{
			System.out.println(link.attr("abs:href"));
			String link_context=" ";
			Element parent=link.parent();
			while (parent!=null&&parent.text()==null&&!parent.text().matches("^"))
			{
				parent=parent.parent();
			}
			if (parent!=null)
				link_context=parent.text();
			link_context.trim();
			System.out.println(link_context);
		}
	}

}
