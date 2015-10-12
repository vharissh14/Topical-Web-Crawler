package crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class tutcrawl {

	public static void main(String[] args) {
		String html = "<div class=\"somename\">" +
				"<p>This is paragraph with <a href=\"home.html\">link</a>. Lorem ipsum</p>" +
				"<p>This is another paragraph, but <span class=\"highlight\">this one is <a href=\"different.html\">different</a> than</span> the first one</p>" +
				"<a href=\"no.html\">this wont be scraped</a>" +
				"</div>";

		Document document = Jsoup.parse(html);

		Elements paragraphs = document.select("p:has(a[href])");

		for (Element paragraph : paragraphs) {
			System.out.println(paragraph.text());
		}
	}
}
