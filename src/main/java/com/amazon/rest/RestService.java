package com.amazon.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.amazon.model.SearchTerm;
import com.amazon.service.AmazonCrawlService;
import com.google.gson.Gson;

@Path("/hello")
public class RestService {

	@Inject
	private AmazonCrawlService crawlService;

	// http://localhost:8080/amazon-tracker/rest/hello/checkCategory?categoryId=327472011
	@GET
	@Path("/checkCategory")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkCategory(@QueryParam("categoryId") String categoryId) {
		System.out.println("checkCategoryRest");
		System.out.println("Category ID : " + categoryId);
		String categoryTitle = crawlService.crawlCategory(categoryId);
		Gson gson = new Gson();
		return gson.toJson(categoryTitle);

	}

	// http://localhost:8080/amazon-tracker/rest/hello/checkAsin?asin=B00LMZLWS6
	@GET
	@Path("/checkAsin")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkAsin(@QueryParam("asin") String asin) {
		System.out.println("checkAsinRest");
		System.out.println("Asin ID : " + asin);
		String productTitle = crawlService.crawlAsin(asin);
		Gson gson = new Gson();
		return gson.toJson(productTitle);

	}

	// http://localhost:8080/amazon-tracker/rest/hello/checkPositionWithKeyword?categoryId=82841031&keyword=kniebandage&searchAsin=B018K58KGM
	// //position 4
	// https://www.amazon.de/Lmeno-Einstellbar-Kniebandage-Kniescheibenbandage-Leichtathletik/dp/B018K58KGM/ref=sr_1_4?s=automotive&ie=UTF8&qid=1468687928&sr=1-4&keywords=kniebandage
	@GET
	@Path("/checkPositionWithKeyword")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkPositionWithKeyword(@QueryParam("categoryId") String categoryId, @QueryParam("keyword") String keyword,
			@QueryParam("searchAsin") String searchAsin) {
		System.out.println("checkPositionWithKeyword");
		System.out.println("cat ID : " + categoryId);
		System.out.println("keyword ID : " + keyword);
		System.out.println("Asin ID : " + searchAsin);
		SearchTerm searchTerm = crawlService.crawlPositionWithKeyword(categoryId, keyword, searchAsin);
		Gson gson = new Gson();
		return gson.toJson(searchTerm);

	}

	@GET
	@Path("/checkPosition")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkPosition(@QueryParam("categoryId") String categoryId, @QueryParam("searchAsin") String searchAsin) {
		System.out.println("checkPosition");
		System.out.println("cat ID : " + categoryId);
		System.out.println("Asin ID : " + searchAsin);
		SearchTerm searchTerm = crawlService.crawlPosition(categoryId, searchAsin);
		Gson gson = new Gson();
		return gson.toJson(searchTerm);

	}

}
