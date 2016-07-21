package com.amazon.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.amazon.model.SearchTerm;

@Named
public class AmazonCrawlService {

	private static final String baseUrl = "http://amazon.de/";
	private static final String categoryUrl = "https://www.amazon.de/s/node=";
	private static final String asinUrl = "https://www.amazon.de/gp/product/";
	private static final String positionUrl1 = "https://www.amazon.de/s/node=";
	private static final String positionUrl2 = "?field-keywords=";
	List<String> asinList = new ArrayList<String>();

	public AmazonCrawlService() {
	}

	public String crawlCategory(String categoryId) {
		Document doc;
		try {
			System.out.println(categoryUrl + categoryId);
			doc = Jsoup.connect(categoryUrl + categoryId).timeout(30000)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
			String categoryTitle = doc.title();
			return categoryTitle;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String crawlAsin(String asin) {
		Document doc;
		try {
			doc = Jsoup.connect(asinUrl + asin).timeout(30000)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
			String title = doc.select("span#productTitle").text();
			return title;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// https://www.amazon.de/s/node=82841031?field-keyword=kniebandage
	public SearchTerm crawlPositionWithKeyword(String categoryId, String keyword, String searchAsin) {
		Document doc;
		try {
			doc = Jsoup.connect(positionUrl1 + categoryId + positionUrl2 + keyword).timeout(30000)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();

			// Pagination
			List<String> siteUrls = handlePagination(doc);

			SearchTerm searchTerm = null;

			for (String url : siteUrls) {
				searchTerm = crawlProductPosition(url, searchAsin);
				if (searchTerm != null) {
					break;
				}
			}
			asinList.clear();
			searchTerm.setKeyword(keyword);
			searchTerm.setCategoryId(Integer.parseInt(categoryId));
			return searchTerm;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public SearchTerm crawlPosition(String categoryId, String searchAsin) {
		Document doc;
		try {
			doc = Jsoup.connect(positionUrl1 + categoryId).timeout(30000)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
			
			// Pagination
			List<String> siteUrls = handlePagination(doc);
			
			SearchTerm searchTerm = null;
			
			for (String url : siteUrls) {
				searchTerm = crawlProductPosition(url, searchAsin);
				if (searchTerm != null) {
					break;
				}
			}
			asinList.clear();
			searchTerm.setCategoryId(Integer.parseInt(categoryId));
			return searchTerm;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gibt ein String Array mit allen Links zu den seiten 1-n zurück
	 * 
	 * @param doc
	 * @return
	 */
	private List<String> handlePagination(Document doc) {
		// Liest die maximaleseitenanzahl aus
		int siteSize = Integer.parseInt(doc.select("span.pagnDisabled").text());
		String naechsteSeite = doc.select("a#pagnNextLink").attr("href");
		List<String> crawlSeiten = new ArrayList<String>();

		for (int i = 1; i < siteSize + 1; i++) {
			String nextPage = naechsteSeite.replace("&page=2", "&page=" + i);
			crawlSeiten.add(baseUrl + nextPage);
		}
		return crawlSeiten;
	}

	/**
	 * Loescht die Kommentare weil Pagination in comments steht und er mehr als 24 produkte pro seite zaelt
	 * @param node
	 */
	private void removeComments(Node node) {
		for (int i = 0; i < node.childNodes().size();) {
			Node child = node.childNode(i);
			if (child.nodeName().equals("#comment"))
				child.remove();
			else {
				removeComments(child);
				i++;
			}
		}
	}

	private SearchTerm crawlProductPosition(String url, String searchAsin) {
		System.out.println(url);
		Document doc;
		try {
			doc = Jsoup.connect(url).timeout(30000)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
			removeComments(doc);
			Elements amzUlElement = doc.select("div.s-result-list-parent-container > ul > li");
			SearchTerm searchTerm = null;
			for (Element element : amzUlElement) {
				// ASIN Xpath = @data-asin
				String asin = element.attr("data-asin");
				System.out.println(" Produkt: " + asin);
				asinList.add(asin);
				if (asin.trim().equals(searchAsin.trim())) {
					// Title div//div//div//div//a/@title
					String title = element.select("div > div > div > div > a").attr("title");
					String link = element.select("div > div > div > div > a").attr("href");

					searchTerm = new SearchTerm();
					searchTerm.setAsin(asin);
					searchTerm.setUrl(link);
					searchTerm.setTitle(title);
					searchTerm.setPosition(asinList.indexOf(searchAsin.trim()) + 1);
					break;
				}
			}
			return searchTerm;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
