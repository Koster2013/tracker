package com.amazon.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.amazon.db.GenericDAO;
import com.amazon.model.SearchTerm;

@ManagedBean
@SessionScoped
public class MainView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	@Inject
	GenericDAO genericDAO;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@PostConstruct
	public void init() {
//		System.out.println("Init");
//
//		SearchTerm searchTerm = new SearchTerm();
//		searchTerm.setUuid(UUID.randomUUID());
//		searchTerm.setAsin("weuwfeferhfre");
//		searchTerm.setCategoryId(6584848);
//		searchTerm.setKeyword("kneibandage");
//		
//		System.out.println("insert");
//		genericDAO.insert(searchTerm);
//		
//		System.out.println("find");
//		SearchTerm searchtermDB = genericDAO.findById(searchTerm, searchTerm.getUuid());
//		System.out.println(searchtermDB.getAsin());
//		
//		System.out.println("delete");
//		genericDAO.delete(searchTerm);
//		
//		System.out.println(genericDAO.findAll(SearchTerm.class));
//		String keyword = "kniebandage";
//
//		Document doc;
//		try {
//
//			String crawlUrl = "http://www.amazon.de/s?field-keywords="
//					+ keyword;
//			doc = Jsoup.connect(crawlUrl).get();
//
//			String baseUrl = "http://www.amazon.de/";
//
//			// Haupt Xpath //div[@id = 'atfResults']//ul//li
//			int seiten = Integer.parseInt(doc.select("span.pagnDisabled")
//					.text());
//			System.out.println("Seiten: " + seiten);
//			// Immer 20 bei der URL
//
//			String naechsteSeite = doc.select("a#pagnNextLink").attr("href");
//			System.out.println("NachsteSeite: " + naechsteSeite);
//
//			List<String> crawlSeiten = new ArrayList<String>();
//
//			for (int i = 0; i < seiten + 1; i++) {
//				String nextPage = naechsteSeite
//						.replace("&page=2", "&page=" + i);
//				crawlSeiten.add(baseUrl + nextPage);
//			}
//
//			System.out.println(crawlSeiten);
//
//			for (String string : crawlSeiten) {
//				System.out.println(getProductsByUrl(string));
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	private List<SearchTerm> getProductsByUrl(String url) {
		Document doc;
		List<SearchTerm> products = new ArrayList<SearchTerm>();
		try {
			doc = Jsoup
					.connect(url)
					.timeout(30000)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
					.get();
			Elements amzUlElement = doc.select("div#atfResults > ul > li");
			for (Element element : amzUlElement) {

				// ASIN Xpath = @data-asin
				String asin = element.attr("data-asin");
				// System.out.println("asin : " + asin);

				// Title div//div//div//div//a/@title
				String title = element.select("div > div > div > div > a")
						.attr("title");
				// System.out.println("Title: " + title);

				String link = element.select("div > div > div > div > a").attr(
						"href");
				// System.out.println("Title: " + link);

				SearchTerm searchTerm = new SearchTerm();
				searchTerm.setAsin(asin);
				searchTerm.setUrl(link);
				searchTerm.setTitle(title);
				products.add(searchTerm);
			}
			return products;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
